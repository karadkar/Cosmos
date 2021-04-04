package io.github.karadkar.sample.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.nasaPicturesAppKoinModules
import io.github.karadkar.sample.utils.ResourceFileReader
import io.github.karadkar.sample.utils.readValue
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

class NasaImageRepositoryTest : KoinTest {
    private lateinit var mockApiService: NasaPicturesApiService

    // This json contains data in ascending order of date
    private val jsonData = ResourceFileReader("nasa_images.json").content

    private lateinit var imageResponseList: List<NasaImageResponse>
    private lateinit var repository: NasaImageRepository

    @Before
    fun setup() {
        mockApiService = mockk()
        startKoin {
            // replace with mock
            val modules = listOf(nasaPicturesAppKoinModules, module {
                single(override = true) { mockApiService }
            })
            loadKoinModules(modules)
        }

        imageResponseList = get<ObjectMapper>().readValue(jsonData)
        repository = get() // from koin
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `fetch images returns list in descending order of date and creates id from index`() {
        every {
            mockApiService.getImages()
        } returns Single.just(imageResponseList.shuffled())

        val observer = repository.fetchImages().test()
        observer.assertComplete()
        val resultList = observer.values().first()

        for (i in resultList.indices) {
            assertThat(resultList[i].id).isEqualTo("id-$i")

            if (i == 0) continue
            assertThat(resultList[i - 1].date).isGreaterThan(resultList[i].date)
        }
    }
}