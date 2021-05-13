package io.github.karadkar.sample.di

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().also {
            // don't fail on unknown properties
            it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

            // in serialization, only include non-null and non empty
            it.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        }
    }

    @Provides
    fun okHttpClient(): OkHttpClient {
        return OkHttpProvider.okHttpClient
    }

    @Provides
    fun retrofitBuilder(objectMapper: ObjectMapper, okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .client(okHttpClient)
    }

    @Provides
    fun retrofit(retrofitBuilder: Retrofit.Builder, httpUrl: HttpUrl): Retrofit {
        return retrofitBuilder.baseUrl(httpUrl).build()
    }
}