package io.github.karadkar.sample.rules

import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * source
 * https://eng.wealthfront.com/2020/12/21/espresso-friendly-bottom-sheet-interactions/
 * https://github.com/material-components/material-components-android/blob/891d00905824d86d00f28c24b9a206575f460997/tests/javatests/com/google/android/material/bottomsheet/BottomSheetBehaviorTest.java
 */
class BottomSheetStateIdlingResource(
    bottomSheetBehavior: BottomSheetBehavior<*>,
    @BottomSheetBehavior.State private val desiredState: Int
) : BottomSheetIdlingResource(bottomSheetBehavior) {

    override fun getName(): String {
        return "BottomSheet awaiting state: $desiredState"
    }

    override fun isDesiredState(@BottomSheetBehavior.State state: Int): Boolean {
        return state == desiredState
    }
}