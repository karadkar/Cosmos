package io.github.karadkar.sample.gridui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import io.github.karadkar.sample.R
import io.github.karadkar.sample.databinding.ActivityNasaPicturesBinding
import io.github.karadkar.sample.detailui.PictureDetailActivity
import io.github.karadkar.sample.utils.addTo
import io.github.karadkar.sample.utils.logError
import io.github.karadkar.sample.utils.logInfo
import io.github.karadkar.sample.utils.visibleOrGone
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.viewmodel.ext.android.viewModel


class NasaPicturesActivity : AppCompatActivity() {
    lateinit var binding: ActivityNasaPicturesBinding
    private val viewModel: NasaPicturesViewModel by viewModel()
    private val disposable = CompositeDisposable()
    private lateinit var adapter: NasaPicturesListAdapter
    private val spanCount = 2

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

        viewModel.submitEvent(NasaPicturesViewEvent.ScreenLoadEvent)
    }

    private fun setupListAdapter() {
        adapter = NasaPicturesListAdapter(this) { clickedItem ->
            viewModel.submitEvent(NasaPicturesViewEvent.ImageClickEvent(imageId = clickedItem.id))
        }
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
                swipeRefreshLayout.isRefreshing = false
            }

        }
    }

    private fun renderViewSate(state: NasaPicturesViewState) {
        binding.apply {
            swipeRefreshLayout.isRefreshing = state.showProgressBar
            rvPictures.visibleOrGone(!state.showProgressBar)
            blankSlate.visibleOrGone(state.showBlankSlate())
        }
        adapter.submitList(state.gridItems)
        logInfo("---< $state")
    }

    private fun triggerViewEffect(effect: NasaPicturesViewEffect) {
        when (effect) {
            is NasaPicturesViewEffect.OpenImageDetailScreenEffect -> {
                startActivity(
                    Intent(this, PictureDetailActivity::class.java)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}