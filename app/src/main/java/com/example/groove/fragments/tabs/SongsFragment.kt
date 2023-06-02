package com.example.groove.fragments.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.adapter.SongAdapter
import com.example.groove.databinding.FragmentSongsBinding
import com.example.groove.viewmodel.MainViewModel

class SongsFragment : Fragment(R.layout.fragment_songs) {

    private lateinit var binding: FragmentSongsBinding
    private lateinit var mainViewModel: MainViewModel


    // Adapter
    private lateinit var songAdapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = (activity as MainActivity).mainViewModel
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
    }

    private fun prepareSongsRecyclerView() {
        songAdapter = SongAdapter()
        binding.rvSongs.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = songAdapter
        }
    }

    private fun observeSongs() {
        mainViewModel.observeAllSongsLiveData().observe(viewLifecycleOwner, Observer {
            songAdapter.differ.submitList(it)
        })
    }
}