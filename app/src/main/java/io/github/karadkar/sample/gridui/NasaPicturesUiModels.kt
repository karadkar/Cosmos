package io.github.karadkar.sample.gridui

import io.github.karadkar.sample.data.NasaImageResponse

data class NasaPictureGridItem(
    val id: String,
    val title: String,
    val imageUrl: String
)


data class NasaPicturesViewState(
    val gridItems: List<NasaPictureGridItem> = emptyList(),
    val showProgressBar: Boolean = false,
    val errorMessage: String? = null
)

sealed class NasaPicturesViewEffect {
    data class OpenImageDetailScreenEffect(val imageId: String) : NasaPicturesViewEffect()
}

sealed class NasaPicturesViewEvent {
    object ScreenLoadEvent : NasaPicturesViewEvent()
    data class ImageClickEvent(val imageId: String) : NasaPicturesViewEvent()
}

sealed class NasaPicturesEventResult {
    data class ScreenLoadResult(val imageResponses: List<NasaImageResponse>) : NasaPicturesEventResult()
    data class ImageClickResult(val imageId: String) : NasaPicturesEventResult()
}