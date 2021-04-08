package io.github.karadkar.sample.rules

import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.squareup.rx2.idler.Rx2Idler
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.TimeUnit

class IdlingResourceRule(okHttpClient: OkHttpClient) : TestRule {

    private val okHttpResource: IdlingResource = OkHttp3IdlingResource.create("okhttp", okHttpClient)

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                IdlingPolicies.setMasterPolicyTimeout(30, TimeUnit.SECONDS)
                IdlingPolicies.setIdlingResourceTimeout(30, TimeUnit.SECONDS)
                RxJavaPlugins.setInitIoSchedulerHandler(Rx2Idler.create("RxJava 2.x IO Scheduler"))
                IdlingRegistry.getInstance().register(okHttpResource)

                base.evaluate()

                IdlingRegistry.getInstance().unregister(okHttpResource)
            }
        }
    }
}