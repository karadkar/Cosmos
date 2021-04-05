package io.github.karadkar.sample.gridui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PictureItemDecorator(private val space: Int, private val spanCount: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildLayoutPosition(view)

        if (position < spanCount) {
            // top row
            outRect.top = space
        }

        if (position % spanCount == 0) {
            // 1st in row
            outRect.left = space
        }

        outRect.right = space
        outRect.bottom = space
    }
}