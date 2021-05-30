package io.github.karadkar.sample

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

open class TestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setupRealm()
    }

    protected open fun setupRealm() {
        Realm.init(this)

        // in-memory realm configuration to avoid flaky tests
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .schemaVersion(1)
                .inMemory()
                .name("test-app.realm")
                .build()
        )
    }
}