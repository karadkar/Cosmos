package io.github.karadkar.sample

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.schibsted.spain.barista.assertion.BaristaAssertions.assertAny
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem
import com.schibsted.spain.barista.interaction.BaristaListInteractions.scrollListToPosition
import com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerBack
import com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward
import io.github.karadkar.sample.data.NasaImageResponse
import io.github.karadkar.sample.gridui.NasaPicturesActivity
import io.github.karadkar.sample.rules.BottomSheetStateIdlingResource
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

    private lateinit var idlingRegistry: IdlingRegistry

    @Before
    fun setup() {
        idlingRegistry = IdlingRegistry.getInstance()
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

        val indexOfItemToClick = 5
        val itemToClick = testData[indexOfItemToClick]
        val previousItem = testData[indexOfItemToClick - 1]
        val nexItem = testData[indexOfItemToClick + 1]

        // click on grid item
        scrollListToPosition(R.id.rv_pictures, indexOfItemToClick)
        assertDisplayedAtPosition(R.id.rv_pictures, indexOfItemToClick, R.id.tv_title, itemToClick.title)
        clickListItem(R.id.rv_pictures, indexOfItemToClick)

        verifyBottomSheetDetails(itemToClick)

        // verify next item
        swipeViewPagerForward()
        verifyBottomSheetDetails(nexItem)
        swipeViewPagerBack()

        // verify previous item
        swipeViewPagerBack()
        verifyBottomSheetDetails(previousItem)
    }

    private fun verifyBottomSheetDetails(data: NasaImageResponse) {
        var bottomSheet: BottomSheetBehavior<MaterialCardView>? = null
        assertAny<MaterialCardView>(R.id.cv_details) {
            bottomSheet = BottomSheetBehavior.from(it)
            bottomSheet != null
        }
        val collapsedResource = BottomSheetStateIdlingResource(bottomSheet!!, BottomSheetBehavior.STATE_COLLAPSED)
        val expandedResource = BottomSheetStateIdlingResource(bottomSheet!!, BottomSheetBehavior.STATE_EXPANDED)

        // verify title on bottom sheet
        assertDisplayed(R.id.tv_picture_detail_title, data.title)

        // click on bottom sheet to expand
        clickOn(R.id.bottom_sheet_head)
        withBottomSheetResource(expandedResource) {
            assertDisplayed(R.id.tv_picture_detail_description, data.explanation)
        }

        // collapse bottom sheet
        clickOn(R.id.bottom_sheet_head)
        withBottomSheetResource(collapsedResource) {
            // nothing
        }
    }

    private fun withBottomSheetResource(
        sheetIdlingResource: IdlingResource,
        actionsAndAssertions: () -> Unit
    ) {
        with(sheetIdlingResource) {
            idlingRegistry.register(this)
            try {
                actionsAndAssertions.invoke()
            } finally {
                idlingRegistry.unregister(this)
            }
        }
    }

}