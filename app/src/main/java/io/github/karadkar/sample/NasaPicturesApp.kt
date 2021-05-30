package io.github.karadkar.sample

import android.app.Application
import com.squareup.picasso.LruCache
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
open class NasaPicturesApp : Application() {
    @Inject
    lateinit var okHttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        setPicassoSingleton()
        setupRealm()
    }

    private fun setPicassoSingleton() {
        val picassoInstance = Picasso.Builder(this)
            .downloader(OkHttp3Downloader(okHttpClient))
            .memoryCache(LruCache(100 * 1024 * 1024))
            .build()
        Picasso.setSingletonInstance(picassoInstance)
    }

    protected open fun setupRealm() {
        Realm.init(this)
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .name("nasa-picture.realm")
                .build()
        )
    }
}