package com.example.groove.fragments.song_tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.adapter.AlbumAdapter
import com.example.groove.databinding.FragmentAlbumsBinding
import com.example.groove.viewmodel.MainSongViewModel
import com.example.groove.viewmodel.MainSongViewModelFactory
import com.example.groove.viewmodel.MainViewModel

class AlbumsFragment : Fragment(R.layout.fragment_albums) {

    private lateinit var binding: FragmentAlbumsBinding

    private lateinit var albumAdapter: AlbumAdapter

    private lateinit var mainViewModel: MainViewModel

    private lateinit var mainSongViewModel: MainSongViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = (activity as MainActivity).mainViewModel
        mainSongViewModel = (activity as MainActivity).mainSongViewModel


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumsBinding.inflate(inflater, container, false)

        prepareSongsRecyclerView()
        observeAlbumHashMap()



        return binding.root
    }

    private fun observeAlbumHashMap() {
        val data = mainSongViewModel.observeAlbumHashMapLiveData()
        Log.d("CHECK", data.value.toString())
        mainSongViewModel.observeAlbumHashMapLiveData().observe(viewLifecycleOwner, Observer {
            Log.d("CHECK", "SUBMITTED LIST TO DIFFER ${it.values.toList()}")
            albumAdapter.differ.submitList(it.values.toList())

        })
    }



    private fun prepareSongsRecyclerView() {
        albumAdapter = AlbumAdapter()
        binding.rvAlbums.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = albumAdapter
            setHasFixedSize(true)
        }
        Log.d("CHECK", "PREPARED RECYCLER VIEW")


    }
}