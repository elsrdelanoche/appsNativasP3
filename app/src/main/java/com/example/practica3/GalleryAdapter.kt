package com.example.practica3

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica3.databinding.GalleryItemBinding
import com.example.practica3.db.MediaItem

class GalleryAdapter(
    private val items: List<MediaItem>,
    private val onClick: (MediaItem) -> Unit,
    private val onLongClick: (MediaItem) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<MediaItem>()
    var multiSelectMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GalleryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun getSelectedItems(): Set<MediaItem> = selectedItems

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: GalleryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MediaItem) {
            binding.mediaTags.text = item.tags
            if (item.isVideo) {
                binding.mediaThumbnail.setImageResource(R.drawable.ic_audio_file)
            } else {
                Glide.with(itemView.context)
                    .load(item.uri)
                    .into(binding.mediaThumbnail)
            }

            itemView.setOnClickListener {
                if (multiSelectMode) {
                    toggleSelection(item)
                } else {
                    onClick(item)
                }
            }

            itemView.setOnLongClickListener {
                if (!multiSelectMode) {
                    multiSelectMode = true
                    onLongClick(item)
                }
                toggleSelection(item)
                true
            }

            if (selectedItems.contains(item)) {
                binding.root.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.selected_item_color))
            } else {
                binding.root.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.transparent))
            }
        }

        private fun toggleSelection(item: MediaItem) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
            } else {
                selectedItems.add(item)
            }
            notifyItemChanged(adapterPosition)
        }
    }
}