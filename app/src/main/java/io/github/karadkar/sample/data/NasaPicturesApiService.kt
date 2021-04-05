package io.github.karadkar.sample.data

import io.reactivex.Observable
import retrofit2.http.GET

interface NasaPicturesApiService {
    // hosted on jsonbin.io
    @GET("b/6069381f1c2ec27de8b09d47")
    fun getImages(): Observable<List<NasaImageResponse>>
}