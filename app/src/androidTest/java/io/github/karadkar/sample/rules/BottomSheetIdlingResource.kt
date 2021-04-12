package io.github.karadkar.sample.rules

import android.view.View
import androidx.test.espresso.IdlingResource
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * source
 * https://eng.wealthfront.com/2020/12/21/espresso-friendly-bottom-sheet-interactions/
 * https://github.com/material-components/material-components-android/blob/891d00905824d86d00f28c24b9a206575f460997/tests/javatests/com/google/android/material/bottomsheet/BottomSheetBehaviorTest.java
 */
abstract class BottomSheetIdlingResource(
  private val bottomSheetBehavior: BottomSheetBehavior<*>
) : IdlingResource, BottomSheetBehavior.BottomSheetCallback() {

    private var isIdle: Boolean = false
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun onSlide(bottomSheet: View, slideOffset: Float) {}

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        val wasIdle = isIdle
        isIdle = isDesiredState(newState)
        if (!wasIdle && isIdle) {
            bottomSheetBehavior.removeBottomSheetCallback(this)
            resourceCallback?.onTransitionToIdle()
        }
    }

    override fun isIdleNow(): Boolean {
        return isIdle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        resourceCallback = callback

        val state = bottomSheetBehavior.state
        isIdle = isDesiredState(state)
        if (isIdle) {
            resourceCallback!!.onTransitionToIdle()
        } else {
            bottomSheetBehavior.addBottomSheetCallback(this)
        }
    }

    abstract fun isDesiredState(@BottomSheetBehavior.State state: Int): Boolean
}