package io.github.karadkar.sample.gridui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.karadkar.sample.R
import io.github.karadkar.sample.databinding.ActivityNasaPicturesBinding
import io.github.karadkar.sample.detailui.PictureDetailActivity
import io.github.karadkar.sample.utils.addTo
import io.github.karadkar.sample.utils.logError
import io.github.karadkar.sample.utils.visibleOrGone
import io.reactivex.disposables.CompositeDisposable


@AndroidEntryPoint
class NasaPicturesActivity : AppCompatActivity() {
    lateinit var binding: ActivityNasaPicturesBinding
    private val viewModel: NasaPicturesViewModel by viewModels()
    private val disposable = CompositeDisposable()
    private lateinit var adapter: NasaPicturesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNasaPicturesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListAdapter()
        viewModel.viewState
            .subscribe({
                renderViewSate(it)
            }, {
                logError("error observing viewState", it)
            })
            .addTo(disposable)

        viewModel.viewEffect
            .subscribe({
                triggerViewEffect(it)
            }, {
                logError("error observing viewEffect", it)
            })
            .addTo(disposable)

        if (savedInstanceState == null) {
            viewModel.submitEvent(NasaPicturesViewEvent.ScreenLoadEvent)
        } // else screen is rotating, skip the screen-load event
    }

    private fun setupListAdapter() {
        adapter = NasaPicturesListAdapter(this) { clickedItem ->
            viewModel.submitEvent(NasaPicturesViewEvent.ImageClickEvent(imageId = clickedItem.id))
        }
        val spanCount = resources.getInteger(R.integer.pictures_span_count)
        val layoutManager = GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)
        val itemDecorator = PictureItemDecorator(
            space = resources.getDimensionPixelSize(R.dimen.grid_space),
            spanCount = spanCount
        )
        binding.apply {
            rvPictures.adapter = adapter
            rvPictures.layoutManager = layoutManager
            rvPictures.addItemDecoration(itemDecorator)
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.submitEvent(NasaPicturesViewEvent.ScreenLoadEvent)
            }

        }
    }

    private fun renderViewSate(state: NasaPicturesViewState) {
        binding.apply {
            swipeRefreshLayout.isRefreshing = state.showProgressBar
            rvPictures.visibleOrGone(state.showGrid())
            blankSlate.visibleOrGone(state.showBlankSlate())
        }
        adapter.submitList(state.gridItems)
    }

    private fun triggerViewEffect(effect: NasaPicturesViewEffect) {
        when (effect) {
            is NasaPicturesViewEffect.OpenImageDetailScreenEffect -> {
                startActivity(
                    PictureDetailActivity.createIntent(this, effect.imageId)
                )
            }
            is NasaPicturesViewEffect.ShowToastScreenEffect -> {
                Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}