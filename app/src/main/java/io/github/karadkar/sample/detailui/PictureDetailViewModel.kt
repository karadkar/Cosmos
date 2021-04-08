package io.github.karadkar.sample.detailui

import androidx.lifecycle.ViewModel
import io.github.karadkar.sample.data.NasaImageRepository
import io.github.karadkar.sample.detailui.PictureDetailEventResult.*
import io.github.karadkar.sample.detailui.PictureDetailViewEvent.*
import io.github.karadkar.sample.utils.logInfo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class PictureDetailViewModel(
    private val repository: NasaImageRepository
) : ViewModel() {

    private val eventEmitter = PublishSubject.create<PictureDetailViewEvent>()
    private lateinit var disposable: Disposable

    val viewState: Observable<PictureDetailViewState>

    init {

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
            )
        }
    }

    private fun Observable<ScreenLoadEvent>.onScreenLoadResult(): Observable<out PictureDetailEventResult> {
        return map { result ->
            val pictureDetails = mutableListOf<PictureDetail>()
            val imageIds = mutableListOf<String>()

            repository.getImages().values.forEach { value ->
                val pictureDetail = value.toPictureDetail()
                pictureDetails.add(pictureDetail)
                imageIds.add(value.id)
            }
            val currentIndex = pictureDetails.indexOfFirst { it.id == result.defaultImageId }
            return@map ScreenLoadResult(
                imageIds = imageIds,
                pictureDetails = pictureDetails,
                currentPageDetail = pictureDetails[currentIndex],
                currentIndex = currentIndex
            )
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
                        imageIds = result.imageIds,
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
}