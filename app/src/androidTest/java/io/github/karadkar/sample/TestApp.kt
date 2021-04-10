package io.github.karadkar.sample

import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TestApp : NasaPicturesApp() {
    override fun onCreate() {
        super.onCreate()
        // other setup functions executes properly
        // e.g setPicassoSingleton()
    }


    /**
     * Excluding some dependencies because overriding them during test doesn't always work eg ./gradlew connectedAndroidTest
     * excluding [apiServiceModule] allows to mock api responses
     *
     * WARNING!
     * Test need to load mock or realm [apiServiceModule] and [appModule]
     * without them test will crash
     */
    override fun setupKoin() {
        startKoin {
            androidLogger()
            androidContext(this@TestApp)
            modules(retrofitModule)// because we need OkHttp for Idling resource
        }
    }

    override fun setupRealm() {
        Realm.init(this)
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .name("test-app.realm")
                .inMemory()
                .build()
        )
    }
}