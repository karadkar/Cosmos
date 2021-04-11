package io.github.karadkar.sample

import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaAssertions.assertAny
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem
import com.schibsted.spain.barista.interaction.BaristaListInteractions.scrollListToPosition
import io.github.karadkar.sample.data.NasaImageResponse
import io.github.karadkar.sample.gridui.NasaPicturesActivity
import io.github.karadkar.sample.rules.IdlingResourceRule
import io.github.karadkar.sample.utils.TestDataProvider
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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

    // creating data from json file
    val testData: List<NasaImageResponse> = TestDataProvider.nasaImageResponseList

    @Before
    fun setup() {

    }

    @After
    fun tearDown() {

    }

    @Test
    fun appShouldDisplay_ListOfImagesWithNames() {
        activityRule.launchActivity(null)

        assertListItemCount(R.id.rv_pictures, testData.size)

        // verify that with api
        testData.forEachIndexed { index, expectedImage ->
            scrollListToPosition(R.id.rv_pictures, index)
            assertDisplayedAtPosition(R.id.rv_pictures, index, R.id.tv_title, expectedImage.title)
        }
    }

    @Test
    fun wheClickedOnGridItem_detailScreenShouldOpen_withSpecificDetails() {
        activityRule.launchActivity(null)

        val indexOfItemToClick = testData.indices.random()
        val itemToClick = testData[indexOfItemToClick]

        // click on grid item
        scrollListToPosition(R.id.rv_pictures, indexOfItemToClick)
        assertDisplayedAtPosition(R.id.rv_pictures, indexOfItemToClick, R.id.tv_title, itemToClick.title)
        clickListItem(R.id.rv_pictures, indexOfItemToClick)

        // verify title
        assertDisplayed(R.id.tv_picture_detail_title, itemToClick.title)

        // click on bottom sheet to expand
        clickOn(R.id.bottom_sheet_head)
        assertAny<ImageView>(R.id.ib_expand_collapse) { it.rotation == 0f }
        //TODO: verify bottom-sheet expanded
        assertDisplayed(R.id.tv_picture_detail_description, itemToClick.explanation)

        // collapse bottom sheet
        clickOn(R.id.bottom_sheet_head)
        assertAny<ImageView>(R.id.ib_expand_collapse) { it.rotation == 180f }
    }
}