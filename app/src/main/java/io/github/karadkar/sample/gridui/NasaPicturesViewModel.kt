package io.github.karadkar.sample.gridui

import androidx.lifecycle.ViewModel
import io.github.karadkar.sample.R
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
        // for replay and auto-connect refer https://tech.instacart.com/view-model-state-preservation-using-autoconnect-d75ee791954b
        eventEmitter
            .doOnNext { logInfo("---> event: $it") }
            .eventToResult()
            .doOnNext { logInfo("---> result: $it") }
            .share()
            .also { result ->
                viewState = result.resultToViewState()
                    .doOnNext { logInfo("---> state: $it") }
                    // replay will provide the last emitted value to new subscribers
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
        // using publish ensure upstream Observable is shared between merged sources
        // without publish, upstream will be subscribed individually by merged sources
        return publish { o ->
            Observable.merge(
                o.ofType(ScreenLoadEvent::class.java).onScreenLoadResult(),
                o.ofType(RefreshScreenEvent::class.java).onRefreshScreenResult(),
                o.ofType(ImageClickEvent::class.java).onImageClickResult()
            )
        }
    }

    private fun Observable<ScreenLoadEvent>.onScreenLoadResult(): Observable<out NasaPicturesEventResult> {
        return this.switchMap { event ->
            val dbResult = repository.getFlowableImageResponseList().map { PicturesResult(it) }.toObservable()
            val apiResult = fetchPictures()
            return@switchMap Observable.merge(dbResult, apiResult)
        }
    }

    private fun Observable<RefreshScreenEvent>.onRefreshScreenResult(): Observable<out NasaPicturesEventResult> {
        return this.switchMap {
            return@switchMap fetchPictures()
        }
    }

    private fun fetchPictures(): Observable<NasaPicturesEventResult> {
        return repository.fetchImages()
            .map<NasaPicturesEventResult> { FetchCompleteResult }
            .onErrorReturn { ErrorResult(it) }
            .startWith(InProgressResult)
    }

    private fun Observable<ImageClickEvent>.onImageClickResult(): Observable<ImageClickResult> {
        return map { ImageClickResult(it.imageId) }
    }

    private fun Observable<NasaPicturesEventResult>.resultToViewState(): Observable<NasaPicturesViewState> {
        return scan(NasaPicturesViewState()) { vs, lceResult ->
            when (lceResult) {
                is PicturesResult -> {
                    val gridItems = lceResult
                        .imageResponses
                        .mapTo(mutableListOf()) { it.mapToPictureGridItem() }

                    vs.copy(gridItems = gridItems)
                }
                is FetchCompleteResult -> vs.copy(showProgressBar = false)
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
                        ShowToastScreenEffect(R.string.generic_error_message)
                    }
                    else -> error("unknown result $eventResult for view-effect")
                }
            }
    }
}
