package io.github.karadkar.sample.di

import io.github.karadkar.sample.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpProvider {
    lateinit var okHttpClient: OkHttpClient

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
}