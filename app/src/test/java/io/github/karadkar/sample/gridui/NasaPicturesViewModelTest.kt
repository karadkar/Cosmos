package io.github.karadkar.sample.gridui

import io.github.karadkar.sample.data.NasaImageRepository
import io.github.karadkar.sample.data.NasaImageResponse
import io.github.karadkar.sample.gridui.NasaPicturesViewEffect.OpenImageDetailScreenEffect
import io.github.karadkar.sample.gridui.NasaPicturesViewEffect.ShowToastScreenEffect
import io.github.karadkar.sample.gridui.NasaPicturesViewEvent.ImageClickEvent
import io.github.karadkar.sample.gridui.NasaPicturesViewEvent.ScreenLoadEvent
import io.github.karadkar.sample.utils.TestAppRxSchedulersProvider
import io.github.karadkar.sample.utils.TestDataProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.junit.After
import org.junit.Before
import org.junit.Test

class NasaPicturesViewModelTest {

    private lateinit var viewModel: NasaPicturesViewModel
    private lateinit var mockRepository: NasaImageRepository
    private val rxSchedulers = TestAppRxSchedulersProvider()
    private val testData: List<NasaImageResponse> = TestDataProvider.nasaImageResponseList
    private val gridItems: MutableList<NasaPictureGridItem> =
        testData.mapTo(mutableListOf()) { it.mapToPictureGridItem() }

    private lateinit var daoValueEmitter: Subject<List<NasaImageResponse>>
    private lateinit var viewStateTester: TestObserver<NasaPicturesViewState>
    private lateinit var viewEffectTester: TestObserver<NasaPicturesViewEffect>

    @Before
    fun setUp() {
        mockRepository = mockk()
        viewModel = NasaPicturesViewModel(mockRepository, rxSchedulers)
        viewStateTester = viewModel.viewState.test()
        viewEffectTester = viewModel.viewEffect.test()
        daoValueEmitter = BehaviorSubject.create()

        every { mockRepository.fetchImages() } returns Observable.just(emptyList())
        every { mockRepository.getFlowableImageResponseList() } returns daoValueEmitter.toFlowable(BackpressureStrategy.LATEST)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `on subscribing we should receive base view state`() {
        viewStateTester.apply {
            assertNotComplete()
            assertValueCount(1)
            assertValueAt(0, NasaPicturesViewState())
        }
    }

    @Test
    fun `ScreenLoadEvent should result with Loading state and then fetches images from repository`() {
        viewModel.submitEvent(ScreenLoadEvent)


        viewStateTester.apply {
            assertValueAt(1, NasaPicturesViewState(showProgressBar = true))
            assertValueAt(2, NasaPicturesViewState(showProgressBar = false))
            assertValueCount(3)// base state + Loading + Success

            daoValueEmitter.onNext(testData) // API saves data to dao
            assertValueCount(4)
            assertValueAt(3, NasaPicturesViewState(gridItems = gridItems))
        }
        verify(exactly = 1) {
            mockRepository.fetchImages()
        }
    }

    @Test
    fun `data should be present in state when it's already saved and api fails`() {
        daoValueEmitter.onNext(testData) // already saved data
        every { mockRepository.fetchImages() } returns Observable.error(Exception("404"))

        viewModel.submitEvent(ScreenLoadEvent)
        viewStateTester.apply {
            assertNoErrors()

            // first state is always empty due to scan operator initialization
            assertValueAt(1, NasaPicturesViewState(showProgressBar = false, gridItems = gridItems))
            assertValueAt(2, NasaPicturesViewState(showProgressBar = true, gridItems = gridItems))
            assertValueAt(3, NasaPicturesViewState(showProgressBar = false, gridItems = gridItems))
        }
    }

    @Test
    fun `repository error should return generic error message`() {
        every { mockRepository.fetchImages() } returns Observable.error(Exception("404 not found"))

        viewModel.submitEvent(ScreenLoadEvent)
        viewStateTester.apply {
            assertNoErrors()
            assertValueCount(3)
            // progress should be disabled in the end
            assertValueAt(2, NasaPicturesViewState(showProgressBar = false))
        }

        viewEffectTester.apply {
            assertNoErrors()
            assertValueCount(1)
            assertValueAt(0, ShowToastScreenEffect("Oops! Something went wrong!"))
        }
    }

    @Test
    fun `ImageClickEvent results in OpenImageDetailScreenEffect`() {
        viewModel.submitEvent(ScreenLoadEvent)
        viewModel.submitEvent(ImageClickEvent(imageId = "id-99"))
        viewEffectTester.apply {
            assertNoErrors()
            assertValueCount(1)
            assertValueAt(0, OpenImageDetailScreenEffect(imageId = "id-99"))
        }
    }
}