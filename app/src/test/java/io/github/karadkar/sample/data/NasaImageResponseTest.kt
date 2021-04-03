package io.github.karadkar.sample.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.utils.configureCommon
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class NasaImageResponseTest {


    lateinit var objectMapper: ObjectMapper

    @Before
    fun setUp() {
        objectMapper = ObjectMapper().also {
            it.configureCommon()
        }
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `json parsing`() {
        val jsonData = """{
            "copyright": "rohit karadkar",
            "date": "2019-12-13",
            "explanation": "some explanation",
            "hdurl": "https://example.com/hd_image.jpg",
            "media_type": "image",
            "service_version": "v1",
            "title": "Orange Planet",
            "url": "https://example.com/sd_image.jpg"
         }""".trimIndent()

        assertThat(jsonData).isNotEmpty()
        val nasaImageResponse = objectMapper.readValue(jsonData, NasaImageResponse::class.java)

        nasaImageResponse.apply {
            assertThat(this).isNotNull()
            assertThat(copyright).isEqualTo("rohit karadkar")
            assertThat(explanation).isEqualTo("some explanation")
            assertThat(imageUrlHd).isEqualTo("https://example.com/hd_image.jpg")
            assertThat(mediaType).isEqualTo("image")
            assertThat(serviceVersion).isEqualTo("v1")
            assertThat(title).isEqualTo("Orange Planet")
            assertThat(imageUrlSd).isEqualTo("https://example.com/sd_image.jpg")
            val sf = SimpleDateFormat(NasaImageResponse.dateFormat).also {
                it.timeZone = TimeZone.getTimeZone("UTC")
            }
            assertThat(date).isEqualTo(sf.parse("2019-12-13"))
        }
    }
}