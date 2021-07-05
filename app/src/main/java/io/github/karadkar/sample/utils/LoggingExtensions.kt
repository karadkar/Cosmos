package io.github.karadkar.sample.utils

import android.util.Log

fun Any.logError(message: String, t: Throwable? = null, tag: String = this.javaClass.simpleName) {
    val _tag = if (tag.isEmpty()) "Unknown" else tag
    Log.e(_tag, message, t)
}

fun Any.logInfo(message: String, tag: String = this.javaClass.simpleName) {
    val _tag = if (tag.isEmpty()) "Unknown" else tag
    Log.i(_tag, message)
}
