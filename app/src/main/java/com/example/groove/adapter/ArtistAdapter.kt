package com.example.groove.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.groove.R
import com.example.groove.databinding.ItemArtistBinding
import com.example.groove.model.Song

class ArtistAdapter : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    init {
        Log.d("CHECK", "@ArtistAdapter Created")
    }

    class ArtistViewHolder(val binding : ItemArtistBinding) : RecyclerView.ViewHolder(binding.root)

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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        return ArtistViewHolder(
            ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val currentArtist = differ.currentList[position]

        Glide.with(holder.itemView)
            .load(currentArtist[0].artUri)
            .circleCrop()
            .apply(RequestOptions().placeholder(R.drawable.ic_song_cover).circleCrop())
            .into(holder.binding.ivArtistCover)

        holder.binding.apply{
            artistTitle.text = currentArtist[0].artist

        }

    }

}