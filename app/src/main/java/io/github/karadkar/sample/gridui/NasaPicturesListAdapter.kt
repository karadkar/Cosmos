package io.github.karadkar.sample.gridui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.github.karadkar.sample.databinding.ItemNasaPictureBinding

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

    inner class ViewHolder(private val binding: ItemNasaPictureBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(item: NasaPictureGridItem) {
            binding.apply {
                tvTitle.text = item.title
                //TODO: placeholder and error image
                Picasso.get().load(item.imageUrl).into(ivImage)
            }
        }

        override fun onClick(v: View?) {
            if (adapterPosition == RecyclerView.NO_POSITION) return
            onClick.invoke(getItem(adapterPosition))
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