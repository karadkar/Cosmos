package io.github.karadkar.sample

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.karadkar.sample.data.NasaImageRepository
import io.github.karadkar.sample.data.NasaImageResponseDao
import io.github.karadkar.sample.data.NasaPicturesApiService
import io.github.karadkar.sample.detailui.PictureDetailViewModel
import io.github.karadkar.sample.gridui.NasaPicturesViewModel
import io.github.karadkar.sample.utils.AppConstants
import io.github.karadkar.sample.utils.AppConstants.ModuleNames
import io.github.karadkar.sample.utils.AppRxSchedulers
import io.github.karadkar.sample.utils.AppRxSchedulersProvider
import io.realm.Realm
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

val retrofitModule = module {

    single<ObjectMapper> {
        return@single ObjectMapper().also {
            // don't fail on unknown properties
            it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

            // in serialization, only include non-null and non empty
            it.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        }
    }

    single<HttpLoggingInterceptor> {
        return@single HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    single<OkHttpClient> {
        return@single OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single<Retrofit.Builder>() {
        return@single Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(get()))
            .client(get<OkHttpClient>())
    }

    single<HttpUrl>(named(ModuleNames.NASA_IMAGES)) {
        return@single AppConstants.BASE_URL_NASA_IMAGES.toHttpUrl()
    }

    single<Retrofit>(qualifier = named(ModuleNames.NASA_IMAGES)) {
        val builder = get<Retrofit.Builder>()
        val httpUrl = get<HttpUrl>(named(ModuleNames.NASA_IMAGES))
        return@single builder.baseUrl(httpUrl).build()
    }

}

val apiServiceModule = module {
    single<NasaPicturesApiService> {
        val retrofit = get<Retrofit>(qualifier = named(ModuleNames.NASA_IMAGES))
        return@single retrofit.create(NasaPicturesApiService::class.java)
    }
}

val appModule = module {
    single<Realm> {
        Realm.getDefaultInstance()
    }

    single<NasaImageResponseDao> { NasaImageResponseDao(realm = get()) }

    single<AppRxSchedulers> {
        return@single AppRxSchedulersProvider()
    }

    single<NasaImageRepository> {
        return@single NasaImageRepository(apiService = get(), imageResponseDao = get(), rxSchedulers = get())
    }

    viewModel {
        return@viewModel NasaPicturesViewModel(repository = get(), rxSchedulers = get())
    }
    viewModel {
        return@viewModel PictureDetailViewModel(repository = get())
    }
}

/**
 * order is important as their dependencies.
 * [appModule] repository needs api service
 * [apiServiceModule] api service needs retrofit
 */
val nasaPicturesAppKoinModules = listOf(retrofitModule, apiServiceModule, appModule)
