package io.github.karadkar.sample.detailui

import androidx.lifecycle.ViewModel
import io.github.karadkar.sample.data.NasaImageRepository

class PictureDetailViewModel(
    private val repository: NasaImageRepository
) : ViewModel() {
    private val pictureDetails = LinkedHashMap<String, PictureDetail>()

    init {
        repository.getImages().values.forEach { value ->
            pictureDetails[value.id] = PictureDetail(
                id = value.id,
                imageUrl = value.imageUrlHd,
                title = value.title,
                description = value.explanation
            )
        }
    }

    fun getPictureDetail(imageId: String): PictureDetail = pictureDetails[imageId]!!
    fun getTotalCount(): Int = pictureDetails.size
    fun getTotalImageIds(): List<String> = pictureDetails.keys.toList()
}