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
import com.example.groove.databinding.ItemSongBinding
import com.example.groove.model.Song
import java.util.concurrent.TimeUnit
import javax.security.auth.callback.Callback

class SongAdapter : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {


    lateinit var onItemClick: ((Song, Int) -> Unit)

    class SongViewHolder(val binding: ItemSongBinding) :
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
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = differ.currentList[position]

        Glide.with(holder.itemView)
            .load(currentSong.artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_song_cover).centerInside())
            .into(holder.binding.ivSongImage)
        Log.d("CHECK", "${position}.plus(${currentSong.artUri})")

        val time = formatDuration(currentSong.duration)

        holder.binding.apply {
            tvSongTitle.text = currentSong.title.toString()
            tvSongArtist.text = currentSong.artist.plus(" • ").plus(time)
        }

        holder.itemView.setOnClickListener {
            onItemClick.invoke(currentSong, position)
        }
    }

    private fun formatDuration(duration: Long):String{
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes* TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }


}