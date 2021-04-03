package io.github.karadkar.sample.data

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonSetter
import java.util.*

class NasaImageResponse {

    @JsonSetter("copyright")
    var copyright: String = ""

    @JsonSetter("")
    @JsonFormat(pattern = dateFormat, shape = JsonFormat.Shape.STRING)
    var date: Date? = null

    @JsonSetter("explanation")
    var explanation: String = ""

    @JsonSetter("hdurl")
    var imageUrlHd: String = ""

    @JsonSetter("media_type")
    var mediaType: String = ""

    @JsonSetter("service_version")
    var serviceVersion: String = ""

    @JsonSetter("title")
    var title: String = ""

    @JsonSetter("url")
    var imageUrlSd: String = ""

    companion object {
        const val dateFormat = "yyyy-MM-dd"
    }
}