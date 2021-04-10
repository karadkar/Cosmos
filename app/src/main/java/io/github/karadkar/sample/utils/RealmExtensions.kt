package io.github.karadkar.sample.utils

import io.reactivex.Completable
import io.reactivex.Flowable
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults

fun Realm.completableTransaction(
    transactionBlock: (realm: Realm) -> Unit
): Completable {
    return Completable.create { completable ->
        this.executeTransactionAsync(
            { transactionRealm -> transactionBlock(transactionRealm) },
            { completable.onComplete() },
            { throwable -> completable.onError(throwable) }
        )
    }
}

fun <T : RealmModel> RealmResults<T>.findAsFlowableList(): Flowable<List<T>> {
    return this.asFlowable()
        .filter { it.isLoaded } // check if async query is completed
        .map { it } // maps to list
}