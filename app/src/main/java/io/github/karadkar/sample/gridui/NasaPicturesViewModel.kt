package io.github.karadkar.sample.gridui

import androidx.lifecycle.ViewModel
import io.github.karadkar.sample.data.NasaImageRepository
import io.github.karadkar.sample.gridui.NasaPicturesEventResult.*
import io.github.karadkar.sample.gridui.NasaPicturesViewEffect.*
import io.github.karadkar.sample.gridui.NasaPicturesViewEvent.*
import io.github.karadkar.sample.utils.AppRxSchedulers
import io.github.karadkar.sample.utils.logError
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

    private fun Observable<NasaPicturesViewEvent>.eventToResult(): Observable<NasaPicturesEventResult> {
        return publish { o ->
            Observable.merge(
                o.ofType(ScreenLoadEvent::class.java).onScreenLoadResult(),
                o.ofType(ImageClickEvent::class.java).onImageClickResult()
            )
        }
    }

    private fun Observable<ScreenLoadEvent>.onScreenLoadResult(): Observable<out NasaPicturesEventResult> {
        return this.switchMap {
            return@switchMap repository.fetchImages()
                .subscribeOn(rxSchedulers.io())
                .observeOn(rxSchedulers.main())
                .map<NasaPicturesEventResult> { ScreenLoadResult(it) }
                .onErrorReturn { ErrorResult(it) }
                .startWith(InProgressResult)
        }
    }

    private fun Observable<ImageClickEvent>.onImageClickResult(): Observable<ImageClickResult> {
        return map { ImageClickResult(it.imageId) }
    }

    private fun Observable<NasaPicturesEventResult>.resultToViewState(): Observable<NasaPicturesViewState> {
        return scan(NasaPicturesViewState()) { vs, lceResult ->
            when (lceResult) {
                is ScreenLoadResult -> {
                    val gridItems = lceResult
                        .imageResponses
                        .mapTo(mutableListOf()) {
                            NasaPictureGridItem(id = it.id, title = it.title, imageUrl = it.imageUrlSd)
                        }
                    vs.copy(
                        showProgressBar = false,
                        gridItems = gridItems
                    )
                }
                is InProgressResult -> vs.copy(showProgressBar = true)
                is ErrorResult -> vs.copy(showProgressBar = false)
                else -> vs.copy()
            }
        }.distinctUntilChanged()
    }

    private fun Observable<NasaPicturesEventResult>.resultToViewEffect(): Observable<NasaPicturesViewEffect> {
        return filter { it is ImageClickResult || it is ErrorResult }
            .map<NasaPicturesViewEffect> { eventResult ->
                when (eventResult) {
                    is ImageClickResult -> OpenImageDetailScreenEffect(eventResult.imageId)
                    is ErrorResult -> {
                        logError("something went wrong", eventResult.throwable)
                        ShowToastScreenEffect("Oops! Something went wrong!")
                    }
                    else -> error("unknown result $eventResult for view-effect")
                }
            }
    }
}
