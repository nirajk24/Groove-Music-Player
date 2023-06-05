package com.example.groove.fragments.song_tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.adapter.AlbumAdapter
import com.example.groove.databinding.FragmentAlbumsBinding
import com.example.groove.viewmodel.MainSongViewModel

class AlbumsFragment : Fragment(R.layout.fragment_albums) {

    private lateinit var binding: FragmentAlbumsBinding
    private lateinit var albumAdapter: AlbumAdapter

    private lateinit var mainSongViewModel: MainSongViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainSongViewModel = (activity as MainActivity).mainSongViewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumsBinding.inflate(inflater, container, false)

        prepareAlbumRecyclerView()
        observeAlbumHashMapLiveData()

        return binding.root
    }


    private fun prepareAlbumRecyclerView() {
        albumAdapter = AlbumAdapter()
        binding.rvAlbums.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = albumAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeAlbumHashMapLiveData() {
        mainSongViewModel.observeAlbumHashMapLiveData().observe(viewLifecycleOwner, Observer {
            binding.tvNoSongs.visibility = View.INVISIBLE

            albumAdapter.differ.submitList(it.values.toList())

        })
    }
}