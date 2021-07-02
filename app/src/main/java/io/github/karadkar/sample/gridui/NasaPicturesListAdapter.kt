package io.github.karadkar.sample.gridui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.github.karadkar.sample.R
import io.github.karadkar.sample.databinding.ItemNasaPictureBinding
import io.github.karadkar.sample.utils.gone
import io.github.karadkar.sample.utils.logError
import io.github.karadkar.sample.utils.visible

class NasaPicturesListAdapter(
    private val context: Context,
    private val onClick: (item: NasaPictureGridItem) -> Unit
) : ListAdapter<NasaPictureGridItem, NasaPicturesListAdapter.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNasaPictureBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemNasaPictureBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, Callback, View.OnTouchListener {
        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnTouchListener(this)
        }

        fun bind(item: NasaPictureGridItem) {
            binding.apply {
                tvTitle.text = item.title
                viewOverlay.gone() // so it stays gone whenever recycled
                Picasso.get().load(item.imageUrl)
                    .error(R.drawable.ic_broken_image)
                    .into(ivImage, this@ViewHolder)
            }
        }

        override fun onClick(v: View?) {
            if (adapterPosition == RecyclerView.NO_POSITION) return
            onClick.invoke(getItem(adapterPosition))
        }

        override fun onSuccess() {
            // picasso image loaded
            binding.apply {
                viewOverlay.visible()
                ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event != null && v != null) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.scaleDown()
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        v.scaleUp()
                    }
                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                        v.scaleUp()
                    }
                }
                logError("")
                return true // true indicates that we've handled .performClick()
            } else {
                return false
            }
        }

        private val minScale = 0.9f
        private val maxScale = 1f
        private val scaleAnimationDuration = 150L

        private fun View.scaleUp() {
            val scaleXAnimation = ObjectAnimator.ofFloat(this, "scaleX", scaleX, maxScale)
            val scaleYAnimation = ObjectAnimator.ofFloat(this, "scaleY", scaleY, maxScale)

            val animSet = AnimatorSet().also {
                it.playTogether(scaleXAnimation, scaleYAnimation)
                it.duration = scaleAnimationDuration
                it.interpolator = LinearInterpolator()
                it.start()
            }
        }

        private fun View.scaleDown() {
            val scaleXAnimation = ObjectAnimator.ofFloat(this, "scaleX", scaleX, minScale)
            val scaleYAnimation = ObjectAnimator.ofFloat(this, "scaleY", scaleY, minScale)
            val animSet = AnimatorSet().also {
                it.playTogether(scaleXAnimation, scaleYAnimation)
                it.duration = scaleAnimationDuration
                it.interpolator = LinearInterpolator()
                it.start()
            }
        }

        override fun onError(e: Exception?) {
            // picasso image error
            binding.apply {
                viewOverlay.gone()
                ivImage.scaleType = ImageView.ScaleType.CENTER
                ivImage.scaleType = ImageView.ScaleType.CENTER
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<NasaPictureGridItem>() {
        override fun areItemsTheSame(oldItem: NasaPictureGridItem, newItem: NasaPictureGridItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NasaPictureGridItem, newItem: NasaPictureGridItem): Boolean {
            return oldItem == newItem
        }
    }
}