package io.github.karadkar.sample.gridui

import androidx.lifecycle.ViewModel
import io.github.karadkar.sample.data.NasaImageRepository
import io.github.karadkar.sample.gridui.NasaPicturesEventResult.*
import io.github.karadkar.sample.gridui.NasaPicturesViewEffect.*
import io.github.karadkar.sample.gridui.NasaPicturesViewEvent.*
import io.github.karadkar.sample.utils.AppRxSchedulers
import io.github.karadkar.sample.utils.Lce
import io.github.karadkar.sample.utils.logInfo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class NasaPicturesViewModel(
    private val repository: NasaImageRepository,
    private val rxSchedulers: AppRxSchedulers
) : ViewModel() {
    private val eventEmitter = PublishSubject.create<NasaPicturesViewEvent>()
    private lateinit var disposable: Disposable

    val viewState: Observable<NasaPicturesViewState>
    val viewEffect: Observable<NasaPicturesViewEffect>

    init {
        eventEmitter
            .doOnNext { logInfo("---> event: $it") }
            .eventToResult()
            .doOnNext { logInfo("---> result: $it") }
            .share()
            .also { result ->
                viewState = result.resultToViewState()
                    .doOnNext { logInfo("---> state: $it") }
                    .replay(1)
                    .autoConnect(1) {
                        disposable = it
                    }

                viewEffect = result
                    .resultToViewEffect()
                    .doOnNext { logInfo("---> effect: $it") }
            }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun submitEvent(event: NasaPicturesViewEvent) {
        eventEmitter.onNext(event)
    }

    private fun Observable<NasaPicturesViewEvent>.eventToResult(): Observable<Lce<out NasaPicturesEventResult>> {
        return publish { o ->
            Observable.merge(
                o.ofType(ScreenLoadEvent::class.java).onScreenLoadResult(),
                o.ofType(ImageClickEvent::class.java).onImageClickResult()
            )
        }
    }

    private fun Observable<ScreenLoadEvent>.onScreenLoadResult(): Observable<out Lce<ScreenLoadResult>> {
        return this.switchMap {
            return@switchMap repository.fetchImages()
                .subscribeOn(rxSchedulers.io())
                .observeOn(rxSchedulers.main())
                .map<Lce<ScreenLoadResult>> { Lce.Content(ScreenLoadResult(it)) }
                .onErrorReturn { Lce.Error(it) }
                .startWith(Lce.Loading())
        }
    }

    private fun Observable<ImageClickEvent>.onImageClickResult(): Observable<Lce<ImageClickResult>> {
        return map { Lce.Content(ImageClickResult(it.imageId)) }
    }

    private fun Observable<Lce<out NasaPicturesEventResult>>.resultToViewState(): Observable<NasaPicturesViewState> {
        return scan(NasaPicturesViewState()) { vs, lceResult ->
            when (lceResult) {
                is Lce.Content -> {
                    when (lceResult.content) {
                        is ScreenLoadResult -> {
                            val gridItems = lceResult.content
                                .imageResponses
                                .mapTo(mutableListOf()) {
                                    NasaPictureGridItem(id = it.id, title = it.title, imageUrl = it.imageUrlSd)
                                }
                            vs.copy(
                                showProgressBar = false,
                                errorMessage = null,
                                gridItems = gridItems
                            )
                        }
                        else -> vs.copy()
                    }

                }
                is Lce.Error -> {
                    vs.copy(
                        showProgressBar = false,
                        errorMessage = "Oops! Something went wrong"
                    )
                }
                is Lce.Loading -> {
                    vs.copy(
                        showProgressBar = true,
                        errorMessage = null
                    )
                }
            }
        }.distinctUntilChanged()
    }

    private fun Observable<Lce<out NasaPicturesEventResult>>.resultToViewEffect(): Observable<NasaPicturesViewEffect> {
        return filter { it is Lce.Content && it.content is ImageClickResult }
            .map { (it as Lce.Content).content }
            .map<NasaPicturesViewEffect> { eventResult ->
                when (eventResult) {
                    is ImageClickResult -> {
                        OpenImageDetailScreenEffect(eventResult.imageId)
                    }
                    else -> error("unknown result $eventResult for view-effect")
                }
            }
    }
}
