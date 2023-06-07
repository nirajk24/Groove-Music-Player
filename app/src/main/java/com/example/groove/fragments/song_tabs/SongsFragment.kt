package com.example.groove.fragments.song_tabs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.activities.PlayerActivity
import com.example.groove.adapter.SongAdapter
import com.example.groove.databinding.FragmentSongsBinding
import com.example.groove.model.Song
import com.example.groove.util.Constant
import com.example.groove.util.utility
import com.example.groove.viewmodel.MainSongViewModel
import com.example.groove.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SongsFragment : Fragment(R.layout.fragment_songs) {

    private lateinit var binding: FragmentSongsBinding
    private lateinit var mainViewModel: MainViewModel

    private lateinit var mainSongViewModel: MainSongViewModel


    // Adapter
    private lateinit var songAdapter: SongAdapter

    // Song List
    private lateinit var allSongListInOrder : List<Song>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = (activity as MainActivity).mainViewModel
        mainSongViewModel = (activity as MainActivity).mainSongViewModel


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareSongsRecyclerView()
        observeSongs()
        onSongItemClick()
    }


    private fun prepareSongsRecyclerView() {
        songAdapter = SongAdapter()
        binding.rvSongs.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = songAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
    }

    private fun observeSongs() {
        mainViewModel.observeAllSongsLiveData().observe(viewLifecycleOwner, Observer {
            allSongListInOrder = it.values.toList()

            if(allSongListInOrder.isNotEmpty()){
                binding.tvNoSongs.visibility = View.GONE  // -> Hiding No Songs Text
                binding.tvSongsNumber.text = allSongListInOrder.size.toString().plus(" Songs")
            }

            songAdapter.differ.submitList(allSongListInOrder)


            lifecycleScope.launch {
                // Initialising Data for Songs, Artist and Album
                mainSongViewModel.setUpAllSongData(allSongListInOrder)
                mainSongViewModel.setUpAlbumHashMapLiveData()
                mainSongViewModel.setUpArtistHashMapLiveData()
            }

        })
    }

    private fun onSongItemClick() {
        songAdapter.onItemClick = { song, position ->
//            val intent = Intent(activity, PlayerActivity::class.java)
//            intent.apply{
//
//                putExtra(Constant.CURRENT_SONG, song)
//                putExtra(Constant.CURRENT_SONG_POSITION, position)
////                putExtra(CURRENT_PLAYLIST, allSongListInOrder)
//            }
//            startActivity(intent)

            (activity as MainActivity).binding.apply {

                // Make Bottom Player Visible
                playerBottomSheet.visibility = View.VISIBLE

                // Set mini Player
                Glide.with(this.root)
                    .load(song.artUri)
                    .centerCrop()
                    .into(this.miniPlayerLayout.ivSongImage)
                miniPlayerLayout.tvSongTitle.text = song.title
                miniPlayerLayout.tvSongArtist.text = song.artist

                // Set Big Player
                Glide.with(this.root)
                    .load(song.artUri)
                    .centerCrop()
                    .into(this.bigPlayerLayout.imgCurrentSongImage)
                bigPlayerLayout.tvCurrentSongTitle.text = song.title
                bigPlayerLayout.tvCurrentSongInfo.text = song.artist
                bigPlayerLayout.tvCurrentSongProgress.text = "00:00"
                bigPlayerLayout.tvCurrentSongTotalTime.text = utility.formatDuration(song.duration)


            }

        }
    }


}