package io.github.karadkar.sample

import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaListAssertions
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaListInteractions
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.github.karadkar.sample.di.BaseUrlModule
import io.github.karadkar.sample.di.OkHttpProvider
import io.github.karadkar.sample.gridui.NasaPicturesActivity
import io.github.karadkar.sample.rules.DeleteRealmRule
import io.github.karadkar.sample.rules.IdlingResourceRule
import io.github.karadkar.sample.utils.TestDataProvider
import io.github.karadkar.sample.utils.toJson
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@UninstallModules(BaseUrlModule::class)
@HiltAndroidTest
class NasaPicturesActivityTest {

    val hiltRule = HiltAndroidRule(this)

    val idlingResourceRule = IdlingResourceRule(OkHttpProvider.okHttpClient)

    val activityRule = ActivityTestRule(NasaPicturesActivity::class.java, true, false)

    @get:Rule
    val ruleChain = RuleChain.outerRule(DeleteRealmRule())
        .around(hiltRule).around(idlingResourceRule).around(activityRule)

    private val mockWebServer: MockWebServer = MockWebServer()
    private val testData = TestDataProvider.nasaImageResponseList.take(5)

    @BindValue
    @JvmField
    var mockHttpUrl: HttpUrl = mockWebServer.url("/")

    @Test
    fun limitedData() {
        mockWebServer.enqueue(MockResponse().setBody(testData.toJson()))
        activityRule.launchActivity(null)

        assertListItemCount(R.id.rv_pictures, testData.size)
        // verify that with api
        testData.forEachIndexed { index, expectedImage ->
            BaristaListInteractions.scrollListToPosition(R.id.rv_pictures, index)
            BaristaListAssertions.assertDisplayedAtPosition(R.id.rv_pictures, index, R.id.tv_title, expectedImage.title)
        }
    }

    @Test
    fun blankSlateAndErrorToast() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(401)
        )
        activityRule.launchActivity(null)
        assertNotDisplayed(R.id.rv_pictures)
        assertDisplayed(R.id.blank_slate)
        // TODO: verify error toast
    }
}