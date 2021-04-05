package io.github.karadkar.sample.data

import io.github.karadkar.sample.utils.logError
import io.reactivex.Observable

class NasaImageRepository(
    private val apiService: NasaPicturesApiService,
) {

    fun fetchImages(): Observable<List<NasaImageResponse>> {
        return apiService.getImages()
            .map { response ->
                return@map response
                    .filter { it.date != null }
                    .sortedByDescending { it.date }
                    .also {
                        it.forEachIndexed { index, nasaImageResponse ->
                            nasaImageResponse.id = "id-$index"
                        }
                    }
            }
    }
}