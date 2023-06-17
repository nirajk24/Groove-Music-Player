package com.example.groove.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groove.databinding.ItemPlaylistBinding
import com.example.groove.model.Playlist

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    class PlaylistViewHolder(val binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<Playlist>() {

        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(
            ItemPlaylistBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val currentPlaylist = differ.currentList[position]

//        Glide.with(holder.itemView)
//            .load(currentPlaylist)
//            .circleCrop()
//            .apply(RequestOptions().placeholder(R.drawable.ic_song_cover).circleCrop())
//            .into(holder.binding.ivArtistCover)

        holder.binding.apply{
            tvPlaylistTitle.text = currentPlaylist.title
            tvPlaylistDetails.text = currentPlaylist.numOfSongs.toString().plus(" Songs • ").plus(currentPlaylist.totalDuration)
        }
    }
}