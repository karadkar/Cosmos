package io.github.karadkar.sample.detailui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class PicturesAdapter(
    fragmentManager: FragmentManager,
    private val imageIds: List<String>
) : FragmentStatePagerAdapter(fragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int = imageIds.size

    override fun getItem(position: Int): Fragment {
        return FragmentPicture.getInstance(imageIds[position])
    }
}