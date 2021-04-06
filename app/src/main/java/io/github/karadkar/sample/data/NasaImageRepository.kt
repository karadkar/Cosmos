package io.github.karadkar.sample.data

import io.github.karadkar.sample.utils.logError
import io.reactivex.Observable

class NasaImageRepository(
    private val apiService: NasaPicturesApiService,
    private val imageCache: LinkedHashMap<String, NasaImageResponse> = LinkedHashMap()
) {

    fun fetchImages(): Observable<List<NasaImageResponse>> {
        return apiService.getImages()
            .doOnError {
                logError("error fetching images", it)
            }
            .map { responses ->
                return@map responses
                    .filter { it.date != null }
                    .sortedByDescending { it.date }
                    .also { _responses ->
                        _responses.forEachIndexed { index, nasaImageResponse ->
                            nasaImageResponse.id = "id-$index"
                            imageCache[nasaImageResponse.id] = nasaImageResponse
                        }
                    }
            }
    }

    fun getImageResponse(imageId: String): NasaImageResponse? = imageCache[imageId]

    fun getImages(): Map<String, NasaImageResponse> = imageCache
}