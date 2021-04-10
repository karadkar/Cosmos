package io.github.karadkar.sample.utils

import io.reactivex.Completable
import io.reactivex.Flowable
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults

/**
 * Testing Realm Async Operations with Rx is not known at this time
 * so replacing original [completableTransaction] with non-async transaction
 */
fun Realm.completableTransaction(
    transactionBlock: (realm: Realm) -> Unit
): Completable {
    return Completable.create { completable ->
        try {
            this.executeTransaction { transactionRealm -> transactionBlock(transactionRealm) }
            completable.onComplete()
        } catch (t: Throwable) {
            completable.onError(t)
        }
    }
}

fun <T : RealmModel> RealmResults<T>.findAsFlowableList(): Flowable<List<T>> {
    return this.asFlowable()
        .filter { it.isLoaded } // check if async query is completed
        .map { it } // maps to list
}