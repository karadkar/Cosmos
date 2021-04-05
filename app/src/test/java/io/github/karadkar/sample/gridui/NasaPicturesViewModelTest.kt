package io.github.karadkar.sample.gridui

import io.github.karadkar.sample.data.NasaImageRepository
import io.github.karadkar.sample.gridui.NasaPicturesViewEffect.OpenImageDetailScreenEffect
import io.github.karadkar.sample.gridui.NasaPicturesViewEvent.ImageClickEvent
import io.github.karadkar.sample.gridui.NasaPicturesViewEvent.ScreenLoadEvent
import io.github.karadkar.sample.utils.TestAppRxSchedulersProvider
import io.github.karadkar.sample.utils.TestDataProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test

class NasaPicturesViewModelTest {

    private lateinit var viewModel: NasaPicturesViewModel
    private lateinit var mockRepository: NasaImageRepository
    private val rxSchedulers = TestAppRxSchedulersProvider()

    private lateinit var viewStateTester: TestObserver<NasaPicturesViewState>
    private lateinit var viewEffectTester: TestObserver<NasaPicturesViewEffect>

    @Before
    fun setUp() {
        mockRepository = mockk()
        viewModel = NasaPicturesViewModel(mockRepository, rxSchedulers)
        viewStateTester = viewModel.viewState.test()
        viewEffectTester = viewModel.viewEffect.test()
        every { mockRepository.fetchImages() } returns Observable.just(TestDataProvider.nasaImageResponseList)
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
        val gridItems = TestDataProvider.nasaImageResponseList.mapTo(mutableListOf()) {
            NasaPictureGridItem(id = it.id, title = it.title, imageUrl = it.imageUrlSd)
        }

        viewStateTester.apply {
            assertValueCount(3) // base state + Loading + result with grid items
            assertValueAt(1, NasaPicturesViewState(showProgressBar = true))
            assertValueAt(2, NasaPicturesViewState(gridItems = gridItems))
        }
        verify(exactly = 1) {
            mockRepository.fetchImages()
        }
    }

    @Test
    fun `repository error should return generic error message`() {
        every { mockRepository.fetchImages() } returns Observable.error(Exception("404 not found"))

        viewModel.submitEvent(ScreenLoadEvent)
        viewStateTester.apply {
            assertValueAt(2, NasaPicturesViewState(errorMessage = "Oops! Something went wrong"))
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