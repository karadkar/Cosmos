package io.github.karadkar.sample

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount
import com.schibsted.spain.barista.interaction.BaristaListInteractions.scrollListToPosition
import io.github.karadkar.sample.gridui.NasaPicturesActivity
import io.github.karadkar.sample.rules.IdlingResourceRule
import io.github.karadkar.sample.utils.TestDataProvider
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.koin.test.inject


// Using Barista for espresso
// https://github.com/AdevintaSpain/Barista
@RunWith(AndroidJUnit4::class)
@LargeTest
class EndToEndTest : KoinTest {
    private val okHttpClient by inject<OkHttpClient>()

    @get:Rule
    val idlingResourceRule = IdlingResourceRule(okHttpClient)

    @get:Rule
    val activityRule = ActivityTestRule(NasaPicturesActivity::class.java, true, false)

    // original modules. touches real api
    private val apiAndAppModule: List<Module> = apiServiceModule + appModule

    @Before
    fun setup() {
        loadKoinModules(apiAndAppModule)
    }

    @After
    fun tearDown() {
        unloadKoinModules(apiAndAppModule)
    }

    @Test
    fun appShouldDisplay_ListOfImagesWithNames() {
        activityRule.launchActivity(null)
        // creating data from json file
        val expectedImages = TestDataProvider.nasaImageResponseList

        assertListItemCount(R.id.rv_pictures, expectedImages.size)

        // verify that with api
        expectedImages.forEachIndexed { index, expectedImage ->
            scrollListToPosition(R.id.rv_pictures, index)
            assertDisplayedAtPosition(R.id.rv_pictures, index, R.id.tv_title, expectedImage.title)
        }
    }
}