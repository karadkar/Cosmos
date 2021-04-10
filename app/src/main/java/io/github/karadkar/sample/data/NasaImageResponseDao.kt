package io.github.karadkar.sample.data

import io.github.karadkar.sample.utils.completableTransaction
import io.github.karadkar.sample.utils.findAllAsFlowableList
import io.reactivex.Completable
import io.reactivex.Flowable
import io.realm.Realm
import io.realm.Sort
import java.io.Closeable

class NasaImageResponseDao(private val realm: Realm) : Closeable {
    fun saveImages(images: List<NasaImageResponse>): Completable {
        return realm.completableTransaction { _realm ->
            _realm.copyToRealmOrUpdate(images)
        }
    }

    fun getFlowableImageResponseList(): Flowable<List<NasaImageResponse>> {
        return realm.where(NasaImageResponse::class.java)
            .sort(NasaImageResponseFields.DATE, Sort.DESCENDING)
            .findAllAsFlowableList()
    }

    fun getImageResponse(id: String): NasaImageResponse? {
        return realm.where(NasaImageResponse::class.java)
            .equalTo(NasaImageResponseFields.ID, id).findFirst()
    }

    override fun close() {
        if (!realm.isClosed) {
            realm.close()
        }
    }
}