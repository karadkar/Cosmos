package io.github.karadkar.sample.utils

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.karadkar.sample.data.NasaImageResponse

object TestDataProvider {
    val nasaImagesJsonData: String
    val nasaImageResponseList: List<NasaImageResponse>

    init {
        nasaImagesJsonData = ResourceFileReader("nasa_images.json").content
        val result: List<NasaImageResponse> = ObjectMapper().configureCommon().readValue(nasaImagesJsonData)
        nasaImageResponseList = result.sortedByDescending { it.date }
        nasaImageResponseList.forEachIndexed { index, response ->
            response.id = "id-$index"
        }
    }
}