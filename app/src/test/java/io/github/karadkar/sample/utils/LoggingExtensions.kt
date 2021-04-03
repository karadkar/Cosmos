package io.github.karadkar.sample.utils

// removes android dependency
// replacing Android Log with println for unit test
fun Any.logError(message: String, t: Throwable? = null) {
    println("${this.javaClass.simpleName}, $message")
    t?.printStackTrace()
}

fun Any.logInfo(message: String) {
    println("${this.javaClass.simpleName}, $message")
}