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
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .name("test-app.realm")
                .build()
        )
    }
}