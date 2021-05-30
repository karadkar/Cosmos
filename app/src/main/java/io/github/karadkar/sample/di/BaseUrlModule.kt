package io.github.karadkar.sample.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.karadkar.sample.utils.AppConstants
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

@Module
@InstallIn(SingletonComponent::class)
object BaseUrlModule {

    @Provides
    fun baseNasaImageUrl(): HttpUrl {
        return AppConstants.BASE_URL_NASA_IMAGES.toHttpUrl()
    }
}