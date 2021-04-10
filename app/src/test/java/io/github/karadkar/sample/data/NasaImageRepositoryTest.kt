package io.github.karadkar.sample.data

import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.utils.TestAppRxSchedulersProvider
import io.github.karadkar.sample.utils.TestDataProvider
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test

class NasaImageRepositoryTest {
    private lateinit var mockApiService: NasaPicturesApiService
    private lateinit var mockDao: NasaImageResponseDao
    private lateinit var repository: NasaImageRepository
    private val imageResponseList: List<NasaImageResponse> = TestDataProvider.nasaImageResponseList

    @Before
    fun setup() {
        mockApiService = mockk()
        mockDao = mockk()
        repository = NasaImageRepository(
            apiService = mockApiService, imageResponseDao = mockDao, rxSchedulers = TestAppRxSchedulersProvider()
        )
        every {
            mockDao.getFlowableImageResponseList()
        } returns Flowable.just(imageResponseList)
    }

    @After
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `fetch images creates id from index`() {
        every {
            mockApiService.getImages()
        } returns Observable.just(imageResponseList.shuffled())

        every { mockDao.saveImages(any()) } returns Completable.complete()

        val observer = repository.fetchImages().test()
        observer.assertComplete()
        val resultList = observer.values().first()

        for (i in resultList.indices) {
            assertThat(resultList[i].id).isEqualTo("id-$i")
        }
    }

    @Test
    fun `getImageResponse returns data from dao`() {
        every { mockDao.getImageResponse(any()) } returns null

        val expectedResponse = imageResponseList.random()
        assertThat(repository.getImageResponse(expectedResponse.id)).isNull()
        verify(exactly = 1) { mockDao.getImageResponse(expectedResponse.id) }


        every { mockDao.getImageResponse(expectedResponse.id) } returns expectedResponse
        assertThat(repository.getImageResponse(expectedResponse.id)).isEqualTo(expectedResponse)
    }

    @Test
    fun `fetchImages should save all the result in dao`() {
        every { mockApiService.getImages() } returns Observable.just(imageResponseList)
        every { mockDao.saveImages(imageResponseList) } returns Completable.complete()

        repository.fetchImages().test().assertComplete()

        verify(exactly = 1) { mockDao.saveImages(imageResponseList) }
    }

    @Test
    fun `when dao fails to store data fetchImages should also fail`() {
        val error = Exception("invalid data")
        every { mockApiService.getImages() } returns Observable.just(imageResponseList)
        every { mockDao.saveImages(imageResponseList) } returns Completable.error(error)

        repository.fetchImages().test().apply {
            assertNotComplete()
            assertNoValues()
            assertError(error)
        }

        verify(exactly = 1) { mockDao.saveImages(imageResponseList) }
    }

    @Test
    fun `getFlowableImageResponseList should return list from dao`() {
        every { mockDao.getFlowableImageResponseList() } returns Flowable.just(imageResponseList)
        repository.getFlowableImageResponseList().test().apply {
            assertValueAt(0, imageResponseList)
        }
    }
}