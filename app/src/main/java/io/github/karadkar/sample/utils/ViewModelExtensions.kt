@file:Suppress("UNCHECKED_CAST")

package io.github.karadkar.sample.utils

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

// https://github.com/tfcporciuncula/hilt-assisted-injection/blob/3cb206df82d9324dcae5e32aa5cf6d967c53e4ac/app/src/main/java/com/tfcporciuncula/hiltassistedinjection/AssistedViewModel.kt
inline fun <reified T : ViewModel> FragmentActivity.assistedViewModel(
    crossinline block: () -> T
) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return block() as T
        }
    }
}

@Suppress("UNCHECKED_CAST")
class BaseVMFactory<T>(val block: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return block() as T
    }
}

inline fun <reified T : ViewModel> FragmentActivity.configureViewModel(noinline block: (() -> T)? = null): T {
    if (block == null) {
        return ViewModelProviders.of(this).get(T::class.java)
    } else {
        return ViewModelProviders.of(this, BaseVMFactory(block)).get(T::class.java)
    }
}

inline fun <reified T : ViewModel> Fragment.configureViewModel(noinline block: (() -> T)? = null): T {
    if (block == null) {
        return ViewModelProviders.of(this).get(T::class.java)
    } else {
        return ViewModelProviders.of(this, BaseVMFactory(block)).get(T::class.java)
    }
}