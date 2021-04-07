package io.github.karadkar.sample.detailui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import io.github.karadkar.sample.databinding.ActivityPictureDetailBinding
import org.koin.android.viewmodel.ext.android.viewModel


class PictureDetailActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPictureDetailBinding

    private val viewModel: PictureDetailViewModel by viewModel()
    private lateinit var bottomSheet: BottomSheetBehavior<MaterialCardView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        binding.vpPictures.adapter = PicturesAdapter(
            supportFragmentManager,
            imageIds = viewModel.getTotalImageIds()
        )
        bottomSheet = BottomSheetBehavior.from(binding.cvDetails)
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheet.addBottomSheetCallback(bottomSheetCallbacks)

        binding.vpPictures.addOnPageChangeListener(picturePageChangeListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onBackPressed() {
        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    private val bottomSheetCallbacks = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            binding.ibExpandCollapse.rotation = if (newState == BottomSheetBehavior.STATE_EXPANDED) 180f else 0f
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }
    }

    private val picturePageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            update(viewModel.getPictureDetail(position))
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    private fun update(detail: PictureDetail) {
        if (bottomSheet.state != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        // TODO: add fade in animation for tile
        binding.apply {
            tvPictureDetailTitle.text = detail.title
            tvPictureDetailAuther.text = "By Rohit Karadkar on 20 Dec 1992"
            tvPictureDetailDescription.text = detail.description
        }
    }
}