package io.github.karadkar.sample.data

import io.reactivex.Single

class NasaImageRepository(
    private val apiService: NasaPicturesApiService,
) {

    fun fetchImages(): Single<List<NasaImageResponse>> {
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