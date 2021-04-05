package io.github.karadkar.sample.utils

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestAppRxSchedulersProvider : AppRxSchedulers {
    override fun main(): Scheduler = Schedulers.trampoline()

    override fun io(): Scheduler = Schedulers.trampoline()

    override fun computation(): Scheduler = Schedulers.trampoline()
}