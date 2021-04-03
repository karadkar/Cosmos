package io.github.karadkar.sample.utils

import io.reactivex.Scheduler

interface AppRxSchedulers {
    fun main(): Scheduler
    fun io(): Scheduler
    fun computation(): Scheduler
}