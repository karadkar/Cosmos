package io.github.karadkar.sample.detailui

import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.data.NasaImageResponse
import io.github.karadkar.sample.utils.TestDataProvider
import org.junit.Test

class PictureDetailModelsTest {

    @Test
    fun `toPictureDetail extension mapping`() {
        assertThat(TestDataProvider.nasaImageResponseList).isNotEmpty()

        TestDataProvider.nasaImageResponseList.forEach { response: NasaImageResponse ->
            val pictureDetail = response.toPictureDetail()
            assertThat(pictureDetail).isNotNull()
            assertEquals(pictureDetail, response)
        }
    }

    @Test(expected = NullPointerException::class)
    fun `toPictureDetail extension throws error on null date`() {
        val response = NasaImageResponse().apply {
            id = "some-id"
            title = "null date response"
            date = null
        }

        assertEquals(response.toPictureDetail(), response)
    }

    private fun assertEquals(pictureDetail: PictureDetail, response: NasaImageResponse) {
        assertThat(pictureDetail.id).isEqualTo(response.id)
        assertThat(pictureDetail.title).isEqualTo(response.title)
        assertThat(pictureDetail.description).isEqualTo(response.explanation)
        assertThat(pictureDetail.date).isEqualTo(response.date)
        assertThat(pictureDetail.author).isEqualTo(response.copyright)
        assertThat(pictureDetail.imageUrl).isEqualTo(response.imageUrlSd)
    }
}