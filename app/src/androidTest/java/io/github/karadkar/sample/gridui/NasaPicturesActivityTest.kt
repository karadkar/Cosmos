package io.github.karadkar.sample.gridui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import io.github.karadkar.sample.R
import io.github.karadkar.sample.appModule
import io.github.karadkar.sample.data.NasaPicturesApiService
import io.github.karadkar.sample.rules.IdlingResourceRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.reactivex.Observable
import okhttp3.OkHttpClient
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject


// Using Barista for espresso
// https://github.com/AdevintaSpain/Barista
@RunWith(AndroidJUnit4::class)
@LargeTest
class NasaPicturesActivityTest : AutoCloseKoinTest() {

    private val okHttpClient by inject<OkHttpClient>()

    @get:Rule
    val idlingResourceRule = IdlingResourceRule(okHttpClient)

    @get:Rule
    val activityRule = ActivityTestRule(NasaPicturesActivity::class.java, true, false)


    private lateinit var mockApiService: NasaPicturesApiService
    private lateinit var mockApiModule: Module
    private lateinit var mockApiAndAppModule: List<Module>

    @Before
    fun setup() {
        mockApiService = mockk()

        mockApiModule = module {
            single(override = true) {
                return@single mockApiService
            }
        }

        // provide mock apiService so we can test error case
        mockApiAndAppModule = mockApiModule + appModule
        loadKoinModules(mockApiAndAppModule)
    }

    @After
    fun tearDown() {
        unloadKoinModules(mockApiAndAppModule)
        unmockkAll()
    }

    @Test
    fun networkError_shouldDisplay_errorToast() {
        every { mockApiService.getImages() } returns Observable.error(Exception("404 not found"))
        activityRule.launchActivity(null)

        // verify no grid
        assertNotDisplayed(R.id.rv_pictures)

        // verify blank slate shown
        assertDisplayed(R.id.blank_slate)
        assertDisplayed(R.string.pull_down_to_refresh)
        assertHasDrawable(R.id.iv_blank_slate, R.drawable.ic_undraw_void)

        // TODO: verify error toast!
        val decorView = activityRule.activity.window.decorView
        onView(withText("Oops! Something went wrong!"))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))
    }
}