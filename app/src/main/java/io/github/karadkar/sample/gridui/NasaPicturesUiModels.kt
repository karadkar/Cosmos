package io.github.karadkar.sample.gridui

import io.github.karadkar.sample.data.NasaImageResponse

data class NasaPictureGridItem(
    val id: String,
    val title: String,
    val imageUrl: String
)


data class NasaPicturesViewState(
    val gridItems: List<NasaPictureGridItem> = emptyList(),
    val showProgressBar: Boolean = false
) {
    fun showBlankSlate(): Boolean = gridItems.isEmpty()
    fun showGrid(): Boolean = gridItems.isNotEmpty()
}

sealed class NasaPicturesViewEffect {
    data class OpenImageDetailScreenEffect(val imageId: String) : NasaPicturesViewEffect()

    // FixMe: use resource string for messages
    data class ShowToastScreenEffect(val message: String) : NasaPicturesViewEffect()
}

sealed class NasaPicturesViewEvent {
    object ScreenLoadEvent : NasaPicturesViewEvent()
    object RefreshScreenEvent : NasaPicturesViewEvent()
    data class ImageClickEvent(val imageId: String) : NasaPicturesViewEvent()
}

sealed class NasaPicturesEventResult {
    data class PicturesResult(val imageResponses: List<NasaImageResponse>) : NasaPicturesEventResult()
    data class ImageClickResult(val imageId: String) : NasaPicturesEventResult()
    object FetchCompleteResult : NasaPicturesEventResult()
    object InProgressResult : NasaPicturesEventResult()
    data class ErrorResult(val throwable: Throwable?) : NasaPicturesEventResult()
}

fun NasaImageResponse.mapToPictureGridItem(): NasaPictureGridItem {
    return NasaPictureGridItem(id = id, title = title, imageUrl = imageUrlSd)
}