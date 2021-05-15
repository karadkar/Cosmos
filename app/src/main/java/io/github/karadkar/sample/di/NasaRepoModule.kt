package io.github.karadkar.sample.di

import android.content.Context
import androidx.annotation.MainThread
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.karadkar.sample.data.NasaImageRepository
import io.github.karadkar.sample.data.NasaImageResponseDao
import io.github.karadkar.sample.data.NasaPicturesApiService
import io.github.karadkar.sample.data.PictureStorageHelper
import io.github.karadkar.sample.utils.AppRxSchedulers
import io.github.karadkar.sample.utils.AppRxSchedulersProvider
import io.realm.Realm
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NasaRepoModule {
    @Provides
    fun nasaPictureService(retrofit: Retrofit): NasaPicturesApiService {
        return retrofit.create(NasaPicturesApiService::class.java)
    }

    @Provides
    fun nasaImageResponseDao(realm: Realm): NasaImageResponseDao {
        return NasaImageResponseDao(realm)
    }

    @Provides
    fun nasaImageRepo(
        apiService: NasaPicturesApiService,
        imageResponseDao: NasaImageResponseDao,
        appRxSchedulers: AppRxSchedulers
    ): NasaImageRepository {
        return NasaImageRepository(apiService, imageResponseDao, appRxSchedulers)
    }

    @Provides
    fun appRxSchedulers(): AppRxSchedulers {
        return AppRxSchedulersProvider()
    }

    @Provides
    fun pictureStorageHelper(@ApplicationContext context: Context): PictureStorageHelper {
        return PictureStorageHelper(context)
    }
}