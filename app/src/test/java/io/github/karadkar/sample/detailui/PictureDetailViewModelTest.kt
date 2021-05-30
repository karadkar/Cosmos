package io.github.karadkar.sample.detailui

import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.data.NasaImageRepository
import io.github.karadkar.sample.data.NasaImageResponse
import io.github.karadkar.sample.data.PictureStorageHelper
import io.github.karadkar.sample.utils.TestAppRxSchedulersProvider
import io.github.karadkar.sample.utils.TestDataProvider
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test

class PictureDetailViewModelTest {

    private lateinit var viewModel: PictureDetailViewModel
    private lateinit var mockRepo: NasaImageRepository
    private lateinit var mockStorageHelper: PictureStorageHelper
    private lateinit var viewStateTester: TestObserver<PictureDetailViewState>

    private val testResponseList: List<NasaImageResponse> = TestDataProvider.nasaImageResponseList
    private val expectedDetailsList = testResponseList.mapTo(mutableListOf()) { it.toPictureDetail() }

    @Before
    fun setUp() {
        mockRepo = mockk()
        mockStorageHelper = mockk()
        every { mockRepo.getFlowableImageResponseList() } returns Flowable.just(testResponseList)

        viewModel = PictureDetailViewModel(
            repository = mockRepo,
            storageHelper = mockStorageHelper,
            rxSchedulers = TestAppRxSchedulersProvider(),
            defaultId = "random-id"
        )
        viewStateTester = viewModel.viewState.test()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `there should be no default state without screen-load event`() {
        viewStateTester.apply {
            assertNoErrors()
            assertNoValues()
        }
    }

    @Test
    fun `screen load event`() {
        // user selects random picture on home screen
        val selectedPageDetail = expectedDetailsList.random()
        val selectedImageId = selectedPageDetail.id
        val selectedIndex = expectedDetailsList.indexOf(selectedPageDetail)

        viewModel.submitEvent(PictureDetailViewEvent.ScreenLoadEvent(selectedImageId))

        viewStateTester.apply {
            assertNoErrors()
            assertValueCount(1)
            val state = PictureDetailViewState(
                pictureDetails = expectedDetailsList,
                currentPageIndex = selectedIndex,
                currentPageDetail = selectedPageDetail,
                bottomSheetIndicatorRotation = 0f
            )
            assertValueAt(0, state)
        }
    }

    @Test
    fun `page select event should update index and pageDetail state`() {
        val selectedPageDetail = expectedDetailsList.random()
        val selectedImageId = selectedPageDetail.id
        val selectedIndex = expectedDetailsList.indexOf(selectedPageDetail)

        viewModel.submitEvent(PictureDetailViewEvent.ScreenLoadEvent(selectedImageId))
        viewModel.submitEvent(PictureDetailViewEvent.PageSelectedEvent(index = 3))
        viewStateTester.apply {
            assertValueCount(2)
            assertThat(values()[0].currentPageIndex).isEqualTo(selectedIndex) // screen load state
            assertThat(values()[1]).isEqualTo(
                PictureDetailViewState(
                    pictureDetails = expectedDetailsList,
                    currentPageDetail = expectedDetailsList[3],
                    currentPageIndex = 3
                )
            ) // last expected state
        }
    }

    @Test
    fun `bottom sheet indicator should rotate to 180 degrees when expanded & 0 degrees when collapsed`() {
        viewModel.submitEvent(PictureDetailViewEvent.ScreenLoadEvent(expectedDetailsList.random().id))
        viewModel.submitEvent(PictureDetailViewEvent.BottomSheetStateChanged(BottomSheetState.Expanded))
        viewModel.submitEvent(PictureDetailViewEvent.BottomSheetStateChanged(BottomSheetState.Collapsed))

        viewStateTester.apply {
            assertNoErrors()
            assertValueCount(3)
            assertThat(values()[0].bottomSheetIndicatorRotation).isEqualTo(0f)
            assertThat(values()[1].bottomSheetIndicatorRotation).isEqualTo(180f)
            assertThat(values()[2].bottomSheetIndicatorRotation).isEqualTo(0f)
        }
    }
}