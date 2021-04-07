package io.github.karadkar.sample.detailui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import io.github.karadkar.sample.R
import io.github.karadkar.sample.databinding.ActivityPictureDetailBinding
import org.koin.android.viewmodel.ext.android.viewModel


class PictureDetailActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityPictureDetailBinding

    private val viewModel: PictureDetailViewModel by viewModel()
    private lateinit var bottomSheet: BottomSheetBehavior<MaterialCardView>
    private lateinit var defaultImageId: String
    private lateinit var authorDetailStringFormat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultImageId = intent?.getStringExtra(keyImageId) ?: error("default image-id no provided")
        binding = ActivityPictureDetailBinding.inflate(layoutInflater)
        authorDetailStringFormat = getString(R.string.format_picture_author)
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
        binding.bottomSheetHead.setOnClickListener(this)

        binding.vpPictures.addOnPageChangeListener(picturePageChangeListener)
        val defaultPagePosition = viewModel.getDefaultPagePosition(defaultImageId)
        binding.vpPictures.currentItem = defaultPagePosition

        if (defaultPagePosition == 0) {
            // in view-pager 0th page is default selected and It doesn't call onPageSelected callback
            // so updating manually
            update(viewModel.getPictureDetail(0))
        }
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

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bottom_sheet_head -> {
                if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                } else if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    private fun update(detail: PictureDetail) {
        if (bottomSheet.state != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        // TODO: add fade in animation for tile
        val authorDetails = if (detail.author.isNotEmpty()) {
            authorDetailStringFormat.format(detail.author, detail.getFormattedDateString())
        } else {
            detail.getFormattedDateString()
        }

        binding.apply {
            tvPictureDetailTitle.text = detail.title
            tvPictureDetailAuther.text = authorDetails
            tvPictureDetailDescription.text = detail.description
        }
    }

    companion object {
        private const val keyImageId = "key.detail.activity.imageId"
        fun createIntent(context: Context, imageId: String): Intent {
            return Intent(context, PictureDetailActivity::class.java).also {
                it.putExtra(keyImageId, imageId)
            }
        }
    }
}