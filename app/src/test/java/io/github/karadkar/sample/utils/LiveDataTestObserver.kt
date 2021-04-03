package io.github.karadkar.sample.utils

import androidx.lifecycle.Observer

class LiveDataTestObserver<T> : Observer<T> {
    var value: T? = null

    override fun onChanged(t: T) {
        this.value = t
    }
}