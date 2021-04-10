package io.github.karadkar.sample.data

import io.realm.Realm
import java.io.Closeable

class NasaImageDao(private val realm: Realm) : Closeable {
    fun saveImages(images: List<NasaImageResponse>) {
        realm.executeTransaction { _realm ->
            _realm.copyToRealmOrUpdate(images)
        }
    }

    override fun close() {
        if (!realm.isClosed) {
            realm.close()
        }
    }
}