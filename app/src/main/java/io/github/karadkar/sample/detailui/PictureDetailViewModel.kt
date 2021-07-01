package io.github.karadkar.sample.detailui

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.karadkar.sample.data.NasaImageRepository
import io.github.karadkar.sample.data.PictureStorageHelper
import io.github.karadkar.sample.detailui.PictureDetailEventResult.*
import io.github.karadkar.sample.detailui.PictureDetailViewEvent.*
import io.github.karadkar.sample.utils.AppRxSchedulers
import io.github.karadkar.sample.utils.logInfo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

@Suppress("UNCHECKED_CAST")
class PictureDetailViewModel @AssistedInject constructor(
    private val repository: NasaImageRepository,
    private val storageHelper: PictureStorageHelper,
    private val rxSchedulers: AppRxSchedulers,
    @Assisted private val defaultId: String // this is only to demonstrate assisted inject
) : ViewModel() {

    /**
     * Read more about assisted inject
     * https://dagger.dev/dev-guide/assisted-injection
     * https://github.com/google/dagger/issues/2287#issuecomment-762108922
     */
    @AssistedFactory
    interface Factory {
        fun create(defaultId: String): PictureDetailViewModel
    }

    private val eventEmitter = PublishSubject.create<PictureDetailViewEvent>()
    private lateinit var disposable: Disposable

    val viewState: Observable<PictureDetailViewState>
    val viewEffect: Observable<PictureDetailViewEffect>

    init {
        logInfo("initialized with $defaultId")

        eventEmitter
            .doOnNext { logInfo("---> event: $it") }
            .eventToResult()
            .doOnNext { logInfo("---> result: $it") }
            .share()
            .also { result ->
                viewState = result
                    .resultToViewState()
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

    fun submitEvent(event: PictureDetailViewEvent) {
        eventEmitter.onNext(event)
    }

    private fun Observable<PictureDetailViewEvent>.eventToResult(): Observable<out PictureDetailEventResult> {
        return publish { o ->
            Observable.merge(
                o.ofType(ScreenLoadEvent::class.java).onScreenLoadResult(),
                o.ofType(PageSelectedEvent::class.java).onPageSelectedResult(),
                o.ofType(BottomSheetStateChanged::class.java).onBottomSheetStateChangedResult(),
                o.ofType(SavePicture::class.java).onSavePictureResult(),
            )
        }
    }

    private fun Observable<out SavePicture>.onSavePictureResult(): Observable<PictureDetailEventResult> {
        return switchMap {
            val result = viewState.first(PictureDetailViewState())
                .map { currentState ->
                    val pictureItem = currentState.pictureDetails[currentState.currentPageIndex]
                    storageHelper.saveImage(pictureItem.imageUrl, pictureItem.title)
                    PictureSaved
                }.subscribeOn(rxSchedulers.io())
                .observeOn(rxSchedulers.main())

            result.toObservable()
        }
    }

    private fun Observable<ScreenLoadEvent>.onScreenLoadResult(): Observable<out PictureDetailEventResult> {
        return switchMap { result ->
            return@switchMap repository.getFlowableImageResponseList().map { imageResponses ->

                val pictureDetails = imageResponses.mapTo(mutableListOf()) { it.toPictureDetail() }
                val currentIndex = pictureDetails.indexOfFirst { it.id == result.defaultImageId }

                return@map ScreenLoadResult(
                    pictureDetails = pictureDetails,
                    currentPageDetail = pictureDetails[currentIndex],
                    currentIndex = currentIndex
                )
            }.toObservable()
        }
    }

    private fun Observable<PageSelectedEvent>.onPageSelectedResult(): Observable<out PictureDetailEventResult> {
        return map { PageSelectedResult(it.index) }
    }

    private fun Observable<BottomSheetStateChanged>.onBottomSheetStateChangedResult(): Observable<out RotateBottomSheetIndicatorResult> {
        return map {
            val rotation = if (it.state == BottomSheetState.Expanded) 180f else 0f
            return@map RotateBottomSheetIndicatorResult(rotation)
        }
    }

    private fun Observable<out PictureDetailEventResult>.resultToViewState(): Observable<PictureDetailViewState> {
        return scan(PictureDetailViewState()) { vs, result ->
            when (result) {
                is ScreenLoadResult -> {
                    vs.copy(
                        pictureDetails = result.pictureDetails,
                        currentPageDetail = result.currentPageDetail,
                        currentPageIndex = result.currentIndex
                    )
                }
                is PageSelectedResult -> {
                    vs.copy(
                        currentPageDetail = vs.pictureDetails[result.index],
                        currentPageIndex = result.index
                    )
                }
                is RotateBottomSheetIndicatorResult -> vs.copy(bottomSheetIndicatorRotation = result.rotation)
                else -> vs
            }
        }.filter { it.currentPageDetail != null }
            .distinctUntilChanged()
    }

    private fun Observable<out PictureDetailEventResult>.resultToViewEffect(): Observable<PictureDetailViewEffect> {
        return this.filter { it is PictureSaved || it is SendImageUriToWallpaperFactory || it is AskStoragePermission }
            .map { result ->
                when (result) {
                    is AskStoragePermission -> PictureDetailViewEffect.AskStoragePermission
                    is PictureSaved -> PictureDetailViewEffect.PictureSaved
                    else -> error("$result not implemented")
                }
            }
    }
}