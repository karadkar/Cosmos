package io.github.karadkar.sample.data

import io.github.karadkar.sample.utils.completableTransaction
import io.reactivex.Completable
import io.realm.Realm
import java.io.Closeable

class NasaImageDao(private val realm: Realm) : Closeable {
    fun saveImages(images: List<NasaImageResponse>): Completable {
        return realm.completableTransaction { _realm ->
            _realm.copyToRealmOrUpdate(images)
        }
    }

    override fun close() {
        if (!realm.isClosed) {
            realm.close()
        }
    }
}