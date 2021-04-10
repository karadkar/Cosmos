package io.github.karadkar.sample.utils

import io.reactivex.Completable
import io.reactivex.Flowable
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery

/**
 * Testing Realm Async Operations with Rx is not known at this time
 * so replacing [Realm.executeTransactionAsync] with [Realm.executeTransaction] for tests
 * see [completableTransaction]
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

/**
 * replacing [RealmQuery.findAllAsync] with [RealmQuery.findAll] for tests
 * see [findAllAsFlowableList]
 */
fun <T : RealmModel> RealmQuery<T>.findAllAsFlowableList(): Flowable<List<T>> {
    return this.findAll().asFlowable()
        .filter { it.isLoaded } // check if async query is completed
        .map { it } // maps to list
}