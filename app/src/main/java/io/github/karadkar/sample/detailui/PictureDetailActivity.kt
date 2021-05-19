package io.github.karadkar.sample.detailui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import io.github.karadkar.sample.R
import io.github.karadkar.sample.databinding.ActivityPictureDetailBinding
import io.github.karadkar.sample.utils.addTo
import io.github.karadkar.sample.utils.logError
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@AndroidEntryPoint
class PictureDetailActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityPictureDetailBinding

    @Inject
    lateinit var factory: PictureDetailViewModel.Factory

    private val viewModel: PictureDetailViewModel by viewModels {
        PictureDetailViewModel.provideFactory(factory, "rohit-id-1")
    }

    private lateinit var bottomSheet: BottomSheetBehavior<MaterialCardView>
    private lateinit var defaultImageId: String
    private lateinit var authorDetailStringFormat: String
    private val disposable = CompositeDisposable()
    private lateinit var adapter: PictureDetailAdapter

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

        bottomSheet = BottomSheetBehavior.from(binding.cvDetails)
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheet.addBottomSheetCallback(bottomSheetCallbacks)
        binding.bottomSheetHead.setOnClickListener(this)
        binding.vpPictures.registerOnPageChangeCallback(picturePageChangeListener)
        adapter = PictureDetailAdapter(this)
        binding.vpPictures.adapter = adapter

        viewModel.viewState
            .subscribe({
                renderViewState(it)
            }, {
                logError("error observing view-state", it)
            }).addTo(disposable)

        viewModel.viewEffect
            .subscribe({ effect ->
                triggerViewEffect(effect)
            }, {
                logError("error observing view-effect", it)
            }).addTo(disposable)

        if (savedInstanceState == null) {
            viewModel.submitEvent(PictureDetailViewEvent.ScreenLoadEvent(defaultImageId))
        } // else screen is rotating skip the event
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.picture_details_menu, menu)
        return true
    }

    private fun triggerViewEffect(effect: PictureDetailViewEffect) {
        when (effect) {
            is PictureDetailViewEffect.PictureSaved -> {
                Toast.makeText(this, "Picture Saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save_picture -> {
                viewModel.submitEvent(PictureDetailViewEvent.SavePicture)
                true
            }
            R.id.menu_set_as_wallpaper -> {
                viewModel.submitEvent(PictureDetailViewEvent.SetAsWallpaper)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun renderViewState(state: PictureDetailViewState) {
        val currentDetail = state.currentPageDetail!!
        updateBottomSheet(currentDetail)

        adapter.submitList(state.pictureDetails)
        // needs update
        if (binding.vpPictures.currentItem != state.currentPageIndex) {
            binding.vpPictures.currentItem = state.currentPageIndex
        }
        binding.apply {
            if (ibExpandCollapse.rotation != state.bottomSheetIndicatorRotation) {
                ibExpandCollapse.animate()
                    .rotation(state.bottomSheetIndicatorRotation)
                    .setDuration(300)
            }
        }
    }

    private fun updateBottomSheet(detail: PictureDetail) {
        // TODO: add fade in animation for tile
        val authorDetails = if (detail.author.isNotEmpty()) {
            authorDetailStringFormat.format(detail.author, detail.getFormattedDateString())
        } else {
            detail.getFormattedDateString()
        }

        binding.apply {
            tvPictureDetailTitle.also { tv ->
                if (tv.text == detail.title) return // avoid animation
                // fade-out before setting new title
                tv.animate().alpha(0f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        tv.text = detail.title
                        tv.animate().alpha(1f).setDuration(300) // fade-in
                    }
                })
            }
            tvPictureDetailAuther.text = authorDetails
            tvPictureDetailDescription.text = detail.description
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
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
            viewModel.submitEvent(PictureDetailViewEvent.BottomSheetStateChanged(newState.toBottomSheetState()))
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }
    }

    private fun Int.toBottomSheetState(): BottomSheetState {
        return when (this) {
            BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetState.Collapsed
            BottomSheetBehavior.STATE_EXPANDED -> BottomSheetState.Expanded
            else -> BottomSheetState.Other
        }
    }

    private val picturePageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.submitEvent(
                PictureDetailViewEvent.PageSelectedEvent(position)
            )
        }
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

    companion object {
        private const val keyImageId = "key.detail.activity.imageId"
        fun createIntent(context: Context, imageId: String): Intent {
            return Intent(context, PictureDetailActivity::class.java).also {
                it.putExtra(keyImageId, imageId)
            }
        }
    }
}