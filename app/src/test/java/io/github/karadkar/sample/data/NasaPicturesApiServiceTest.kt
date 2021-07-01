package io.github.karadkar.sample.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.di.NasaRepoModule
import io.github.karadkar.sample.di.OkHttpProvider
import io.github.karadkar.sample.di.RetrofitModule
import io.github.karadkar.sample.utils.ResourceFileReader
import io.github.karadkar.sample.utils.readValue
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class NasaPicturesApiServiceTest {
    lateinit var mockWebServer: MockWebServer
    private val nasaImagesJsonData = ResourceFileReader("nasa_images.json").content
    private lateinit var apiService: NasaPicturesApiService
    private lateinit var objectMapper: ObjectMapper

    @Before
    fun setup() {
        mockWebServer = MockWebServer()

        val retrofit: Retrofit
        RetrofitModule.apply {
            objectMapper = objectMapper()
            val retrofitBuilder = retrofitBuilder(objectMapper, OkHttpProvider.okHttpClient)
            retrofit = retrofit(retrofitBuilder, mockWebServer.url("/"))
        }
        apiService = NasaRepoModule.nasaPictureService(retrofit)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `get images returns response similar to json`() {
        mockWebServer.enqueue(MockResponse().setBody(nasaImagesJsonData))

        val testSub = apiService.getImages().test()
        testSub.assertComplete()

        val expectedImageResponseList: List<NasaImageResponse> = objectMapper.readValue(nasaImagesJsonData)

        testSub.assertValue(expectedImageResponseList)

        assertThat(mockWebServer.takeRequest().path).isEqualTo("/b/6069381f1c2ec27de8b09d47")
    }
}