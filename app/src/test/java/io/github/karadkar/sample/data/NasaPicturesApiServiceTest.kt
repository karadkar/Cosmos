package io.github.karadkar.sample.data

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.nasaPicturesAppKoinModules
import io.github.karadkar.sample.utils.AppConstants
import io.github.karadkar.sample.utils.ResourceFileReader
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

class NasaPicturesApiServiceTest : KoinTest {
    lateinit var mockWebServer: MockWebServer
    private val nasaImagesJsonData = ResourceFileReader("nasa_images.json").content

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        startKoin {
            val testModules = listOf(nasaPicturesAppKoinModules, module {
                // override base url used for retrofit with mock-web-server
                single<HttpUrl>(named(AppConstants.ModuleNames.NASA_IMAGES), override = true) {
                    return@single mockWebServer.url("/")
                }
            })
            loadKoinModules(testModules)
        }
        mockWebServer.start()
    }

    @After
    fun teardown() {
        stopKoin()
        mockWebServer.shutdown()
    }

    @Test
    fun `get images returns response similar to json`() {
        mockWebServer.enqueue(MockResponse().setBody(nasaImagesJsonData))

        val apiService = get<NasaPicturesApiService>() // from koin
        val testSub = apiService.getImages().test()
        testSub.assertComplete()

        val expectedImageResponseList: List<NasaImageResponse> =
            get<ObjectMapper>().readValue(nasaImagesJsonData, object : TypeReference<List<NasaImageResponse>>() {})

        testSub.assertValue(expectedImageResponseList)

        assertThat(mockWebServer.takeRequest().path).isEqualTo("/b/6069381f1c2ec27de8b09d47")
    }
}