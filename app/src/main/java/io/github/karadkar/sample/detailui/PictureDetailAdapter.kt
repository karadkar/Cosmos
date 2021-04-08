package io.github.karadkar.sample.detailui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.github.karadkar.sample.R
import io.github.karadkar.sample.databinding.ItemPictureDetailBinding
import io.github.karadkar.sample.utils.gone
import io.github.karadkar.sample.utils.visible

class PictureDetailAdapter(
    private val context: Context,
) : ListAdapter<PictureDetail, PictureDetailAdapter.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPictureDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPictureDetailBinding) : RecyclerView.ViewHolder(binding.root),
        Callback {

        fun bind(item: PictureDetail) {
            binding.progressBar.visible()
            Picasso.get()
                .load(item.imageUrl)
                .error(R.drawable.ic_broken_image)
                .into(binding.tivPicture, this)
        }

        override fun onSuccess() {
            binding.apply {
                progressBar.gone()
                tivPicture.scaleType = ImageView.ScaleType.FIT_CENTER
                tivPicture.isZoomEnabled = true
            }
        }

        override fun onError(e: Exception?) {
            binding.apply {
                progressBar.gone()
                tivPicture.scaleType = ImageView.ScaleType.CENTER
                tivPicture.isZoomEnabled = false
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<PictureDetail>() {
        override fun areItemsTheSame(oldItem: PictureDetail, newItem: PictureDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PictureDetail, newItem: PictureDetail): Boolean {
            return oldItem == newItem
        }
    }
}