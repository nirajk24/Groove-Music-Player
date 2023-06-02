package com.example.groove.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groove.databinding.SongItemBinding
import com.example.groove.model.Song
import javax.security.auth.callback.Callback

class SongAdapter : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(val binding: SongItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id  // returns true if item id is same
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem  // returns true if the items are same
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            SongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = differ.currentList[position]

        Glide.with(holder.itemView)
            .load(currentSong.artUri)
            .into(holder.binding.ivSongImage)

        holder.binding.apply {
            tvSongTitle.text = currentSong.title.toString()
            tvSongArtist.text = currentSong.artist.plus(" • ").plus(currentSong.duration)
        }
    }


}