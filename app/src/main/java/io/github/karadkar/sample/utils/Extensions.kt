package io.github.karadkar.sample.utils

import android.view.View
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addTo(disposable: CompositeDisposable) {
    disposable.add(this)
}

fun View.visibleOrGone(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun ObjectMapper.configureCommon(): ObjectMapper {
    // don't fail on unknown properties
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    // in serialization, only include non-null and non empty
    this.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

    return this
}