package io.github.karadkar.sample.data

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class NasaImageResponse : RealmObject() {

    @JsonIgnore
    @PrimaryKey
    var id: String = ""

    @JsonProperty("copyright")
    var copyright: String = ""

    @JsonProperty("")
    @JsonFormat(pattern = dateFormat, shape = JsonFormat.Shape.STRING)
    var date: Date? = null

    @JsonProperty("explanation")
    var explanation: String = ""

    @JsonProperty("hdurl")
    var imageUrlHd: String = ""

    @JsonProperty("media_type")
    var mediaType: String = ""

    @JsonProperty("service_version")
    var serviceVersion: String = ""

    @JsonProperty("title")
    var title: String = ""

    @JsonProperty("url")
    var imageUrlSd: String = ""

    companion object {
        const val dateFormat = "yyyy-MM-dd"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NasaImageResponse) return false

        if (id != other.id) return false
        if (copyright != other.copyright) return false
        if (date != other.date) return false
        if (explanation != other.explanation) return false
        if (imageUrlHd != other.imageUrlHd) return false
        if (mediaType != other.mediaType) return false
        if (serviceVersion != other.serviceVersion) return false
        if (title != other.title) return false
        if (imageUrlSd != other.imageUrlSd) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + copyright.hashCode()
        result = 31 * result + (date?.hashCode() ?: 0)
        result = 31 * result + explanation.hashCode()
        result = 31 * result + imageUrlHd.hashCode()
        result = 31 * result + mediaType.hashCode()
        result = 31 * result + serviceVersion.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + imageUrlSd.hashCode()
        return result
    }
}