package io.github.karadkar.sample.utils

sealed class Lce<T> {
    class Loading<T> : Lce<T>()
    class Content<T>(val content: T) : Lce<T>()
    class Error<T>(val t: Throwable) : Lce<T>()
}