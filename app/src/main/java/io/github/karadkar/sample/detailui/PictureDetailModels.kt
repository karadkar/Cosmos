package io.github.karadkar.sample.detailui

import android.net.Uri
import io.github.karadkar.sample.data.NasaImageResponse
import java.text.SimpleDateFormat
import java.util.*

data class PictureDetail(
    val id: String,
    val imageUrl: String,
    val title: String,
    val author: String,
    val date: Date,
    val description: String,
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
) {

    fun getFormattedDateString(): String = dateFormat.format(date)

}

sealed class BottomSheetState {
    object Collapsed : BottomSheetState()
    object Expanded : BottomSheetState()
    object Other : BottomSheetState()
}

sealed class PictureDetailViewEvent {
    data class ScreenLoadEvent(val defaultImageId: String) : PictureDetailViewEvent()
    data class PageSelectedEvent(val index: Int) : PictureDetailViewEvent()
    data class BottomSheetStateChanged(val state: BottomSheetState) : PictureDetailViewEvent()
    object SavePicture : PictureDetailViewEvent()
    object SetAsWallpaper : PictureDetailViewEvent()
}

sealed class PictureDetailEventResult {
    data class ScreenLoadResult(
        val pictureDetails: List<PictureDetail>,
        val currentPageDetail: PictureDetail,
        val currentIndex: Int
    ) : PictureDetailEventResult()

    data class PageSelectedResult(val index: Int) : PictureDetailEventResult()
    data class RotateBottomSheetIndicatorResult(val rotation: Float) : PictureDetailEventResult()
    object AskStoragePermission : PictureDetailEventResult()
    object PictureSaved : PictureDetailEventResult()
    data class SendImageUriToWallpaperFactory(val imageUri: Uri) : PictureDetailEventResult()
}

data class PictureDetailViewState(
    val pictureDetails: List<PictureDetail> = emptyList(),
    val currentPageDetail: PictureDetail? = null,
    val currentPageIndex: Int = 0,
    val bottomSheetIndicatorRotation: Float = 0f
)

sealed class PictureDetailViewEffect {
    object AskStoragePermission : PictureDetailViewEffect()
    object PictureSaved : PictureDetailViewEffect()
    data class SetAsWallpaper(val imageUri: Uri) : PictureDetailViewEffect()
}

fun NasaImageResponse.toPictureDetail(): PictureDetail {
    return PictureDetail(
        id = this.id,
        imageUrl = this.imageUrlSd,
        title = this.title,
        author = this.copyright,
        date = this.date!!,
        description = this.explanation
    )
}