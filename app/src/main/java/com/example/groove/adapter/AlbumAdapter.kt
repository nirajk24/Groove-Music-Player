package com.example.groove.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.groove.R
import com.example.groove.databinding.ItemAlbumBinding
import com.example.groove.model.Song

class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(val binding : ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object: DiffUtil.ItemCallback<ArrayList<Song>>() {


        override fun areItemsTheSame(oldItem: ArrayList<Song>, newItem: ArrayList<Song>): Boolean {
            return oldItem[0] == newItem[0]
        }

        override fun areContentsTheSame(
            oldItem: ArrayList<Song>,
            newItem: ArrayList<Song>
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val currentAlbum = differ.currentList[position]

        Glide.with(holder.itemView)
            .load(currentAlbum[0].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_song_cover).centerInside())
            .into(holder.binding.ivSAlbumCover)

        holder.binding.apply{
            albumTitle.text = currentAlbum[0].album

        }

    }

}