package io.github.karadkar.sample.data

import io.github.karadkar.sample.utils.logError
import io.reactivex.Flowable
import io.reactivex.Observable

class NasaImageRepository(
    private val apiService: NasaPicturesApiService,
    private val imageResponseDao: NasaImageResponseDao
) {

    fun fetchImages(): Observable<List<NasaImageResponse>> {
        return apiService.getImages()
            .doOnError {
                logError("error fetching images", it)
            }
            .map { responses ->
                return@map responses
                    .filter { it.date != null }
                    .also { _responses ->
                        _responses.forEachIndexed { index, nasaImageResponse ->
                            nasaImageResponse.id = "id-$index"
                        }
                    }
            }.flatMap { responses ->
                return@flatMap imageResponseDao.saveImages(responses)
                    .andThen(Observable.just(responses))
            }
    }

    fun getImageResponse(imageId: String): NasaImageResponse? = imageResponseDao.getImageResponse(imageId)

    fun getImages(): Map<String, NasaImageResponse> = emptyMap()

    fun getFlowableImageResponseList(): Flowable<List<NasaImageResponse>> =
        imageResponseDao.getFlowableImageResponseList()
}