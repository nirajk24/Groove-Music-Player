package com.example.groove.fragments.song_tabs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.activities.PlayerActivity
import com.example.groove.adapter.SongAdapter
import com.example.groove.databinding.FragmentSongsBinding
import com.example.groove.model.Song
import com.example.groove.util.Constant
import com.example.groove.viewmodel.MainSongViewModel
import com.example.groove.viewmodel.MainViewModel

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
        }
    }

    private fun observeSongs() {
        mainViewModel.observeAllSongsLiveData().observe(viewLifecycleOwner, Observer {
            binding.tvNoSongs.visibility = View.INVISIBLE  // -> Hiding No Songs Text

            allSongListInOrder = it.values.toList()

            // Initialising Data for Songs, Artist and Album
            mainSongViewModel.setUpAllSongData(allSongListInOrder)
            mainSongViewModel.setUpAlbumHashMapLiveData()
            mainSongViewModel.setUpArtistHashMapLiveData()

            songAdapter.differ.submitList(allSongListInOrder)
        })
    }

    private fun onSongItemClick() {
        songAdapter.onItemClick = { song, position ->
            val intent = Intent(activity, PlayerActivity::class.java)
            intent.apply{

                putExtra(Constant.CURRENT_SONG, song)
                putExtra(Constant.CURRENT_SONG_POSITION, position)
//                putExtra(CURRENT_PLAYLIST, allSongListInOrder)
            }
            startActivity(intent)
        }
    }


}