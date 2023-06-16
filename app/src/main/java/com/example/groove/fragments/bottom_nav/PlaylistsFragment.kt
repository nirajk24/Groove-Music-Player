package com.example.groove.fragments.bottom_nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.databinding.FragmentPlaylistsBinding
import com.example.groove.viewmodel.MainViewModel
import com.example.groove.viewmodel.PlayerViewModel
import com.example.groove.viewmodel.PlaylistViewModel


class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {

    private lateinit var binding: FragmentPlaylistsBinding

    private lateinit var mainViewModel: MainViewModel
    private lateinit var playerViewModel: PlayerViewModel

    private lateinit var playlistViewModel : PlaylistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding  = FragmentPlaylistsBinding.inflate(inflater, container, false)

        mainViewModel = (activity as MainActivity).mainViewModel
        playerViewModel = (activity as MainActivity).playerViewModel

        playlistViewModel = ViewModelProvider(this)[PlaylistViewModel::class.java]


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observePlaylistLiveData()

        setPlaylistRecyclerView()
    }

    private fun observePlaylistLiveData() {
        mainViewModel.observeAllPlaylistLiveData().observe(viewLifecycleOwner, Observer { playlistList ->
            mainViewModel.observeAllSongsLiveData().value?.let {
                playlistViewModel.setPlaylistSongs(playlistList,
                    it
                )
            }
        })
    }

    private fun setPlaylistRecyclerView() {

    }


}