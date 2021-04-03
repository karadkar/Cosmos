package io.github.karadkar.sample.utils

import android.util.Log

fun Any.logError(message: String, t: Throwable? = null) {
    Log.e(this.javaClass.simpleName, message, t)
}

fun Any.logInfo(message: String) {
    Log.i(this.javaClass.simpleName, message)
}
