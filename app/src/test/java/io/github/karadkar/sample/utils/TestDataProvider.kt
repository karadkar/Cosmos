package io.github.karadkar.sample.utils

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.karadkar.sample.data.NasaImageResponse

object TestDataProvider {
    val nasaImagesJsonData: String
    val nasaImageResponseList: List<NasaImageResponse>

    init {
        nasaImagesJsonData = ResourceFileReader("nasa_images.json").content
        nasaImageResponseList = ObjectMapper().configureCommon().readValue(nasaImagesJsonData)
    }
}