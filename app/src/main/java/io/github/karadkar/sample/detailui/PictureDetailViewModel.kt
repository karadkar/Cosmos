package io.github.karadkar.sample.detailui

import androidx.lifecycle.ViewModel
import io.github.karadkar.sample.data.NasaImageRepository

class PictureDetailViewModel(
    private val repository: NasaImageRepository
) : ViewModel() {
    private val pictureDetails = LinkedHashMap<String, PictureDetail>()
    private val imageIds = mutableListOf<String>()

    init {
        repository.getImages().values.forEach { value ->
            pictureDetails[value.id] = PictureDetail(
                id = value.id,
                imageUrl = value.imageUrlHd,
                title = value.title,
                description = value.explanation
            )
            imageIds.add(value.id)
        }
    }

    fun getPictureDetail(imageId: String): PictureDetail = pictureDetails[imageId]!!
    fun getPictureDetail(indexPosition: Int): PictureDetail = pictureDetails[imageIds[indexPosition]]!!
    fun getTotalCount(): Int = pictureDetails.size
    fun getTotalImageIds(): List<String> = imageIds
}