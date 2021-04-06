package io.github.karadkar.sample.data

import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.utils.TestDataProvider
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test

class NasaImageRepositoryTest {
    private lateinit var mockApiService: NasaPicturesApiService
    private lateinit var repository: NasaImageRepository
    private lateinit var imageCache: LinkedHashMap<String, NasaImageResponse>
    private val imageResponseList: List<NasaImageResponse> = TestDataProvider.nasaImageResponseList

    @Before
    fun setup() {
        mockApiService = mockk()
        imageCache = LinkedHashMap()
        repository = NasaImageRepository(apiService = mockApiService, imageCache = imageCache)
    }

    @After
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `fetch images returns list in descending order of date and creates id from index`() {
        every {
            mockApiService.getImages()
        } returns Observable.just(imageResponseList.shuffled())

        val observer = repository.fetchImages().test()
        observer.assertComplete()
        val resultList = observer.values().first()

        for (i in resultList.indices) {
            assertThat(resultList[i].id).isEqualTo("id-$i")

            if (i == 0) continue
            assertThat(resultList[i - 1].date).isGreaterThan(resultList[i].date)
        }
    }

    @Test
    fun `getImageResponse should return null when images are not fetched before`() {
        every { mockApiService.getImages() } returns Observable.just(imageResponseList)
        val expectedId = imageResponseList.random().id
        assertThat(repository.getImageResponse(expectedId)).isNull()

        repository.fetchImages().test().assertComplete()
        assertThat(repository.getImageResponse(expectedId)).isNotNull()
    }

    @Test
    fun `fetchImages should cache all the result in hash-map`() {
        every { mockApiService.getImages() } returns Observable.just(imageResponseList)
        repository.fetchImages().test().assertComplete()

        imageResponseList.forEach { expectedResponse ->
            assertThat(imageCache[expectedResponse.id]).apply {
                isNotNull()
                isEqualTo(expectedResponse)
            }
        }
    }

    @Test
    fun `image-response cache should retain descending date sort order`() {
        every { mockApiService.getImages() } returns Observable.just(imageResponseList)
        repository.fetchImages().test().assertComplete()
        val responses = imageCache.values.toList()
        responses.forEachIndexed { index, response ->
            if (index > 0) {
                val previousResponse = responses[index - 1]
                assertThat(previousResponse.date).isGreaterThan(response.date)
            }
        }
    }
}