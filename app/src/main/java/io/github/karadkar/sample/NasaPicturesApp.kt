package io.github.karadkar.sample

import android.app.Application
import com.squareup.picasso.LruCache
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

open class NasaPicturesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setupKoin()
        setPicassoSingleton()
        setupRealm()
    }

    protected open fun setupKoin() {
        startKoin {
            androidLogger()
            androidContext(this@NasaPicturesApp)
            modules(nasaPicturesAppKoinModules)
        }
    }

    private fun setPicassoSingleton() {
        val okHttpClient by inject<OkHttpClient>() // koin
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