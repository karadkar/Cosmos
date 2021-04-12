package io.github.karadkar.sample.gridui

import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.data.NasaImageResponse
import io.github.karadkar.sample.utils.TestDataProvider
import org.junit.Test

class NasaPicturesUiModelTest {
    private val testData: List<NasaImageResponse> = TestDataProvider.nasaImageResponseList
    private val pictureGridItems =
        TestDataProvider.nasaImageResponseList.mapTo(mutableListOf()) { it.mapToPictureGridItem() }

    @Test
    fun nasaPicturesViewState() {
        val gridItems = listOf(NasaPictureGridItem(id = "1", title = "legends", imageUrl = "www.example.com/url.jpeg"))
        // with grid items
        NasaPicturesViewState(gridItems = gridItems).apply {
            assertThat(this.showGrid()).isTrue()
            assertThat(this.showBlankSlate()).isFalse()
        }

        // without grid items
        NasaPicturesViewState(gridItems = emptyList()).apply {
            assertThat(this.showGrid()).isFalse()
            assertThat(this.showBlankSlate()).isTrue()
        }
    }

    @Test
    fun `NasaImageResponse mapping to NasaPictureGridItem`() {
        testData.forEach { nasaImageResponse ->
            val gridItem: NasaPictureGridItem = nasaImageResponse.mapToPictureGridItem()
            assertThat(gridItem.id).isEqualTo(nasaImageResponse.id)
            assertThat(gridItem.title).isEqualTo(nasaImageResponse.title)
            assertThat(gridItem.imageUrl).isEqualTo(nasaImageResponse.imageUrlSd)
        }
    }
}