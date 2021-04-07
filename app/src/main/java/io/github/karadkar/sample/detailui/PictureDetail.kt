package io.github.karadkar.sample.detailui

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
