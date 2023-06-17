package com.example.groove.fragments.song_tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.adapter.SongAdapter
import com.example.groove.databinding.FragmentAlbumSongsBinding
import com.example.groove.model.Song
import com.example.groove.viewmodel.MainSongViewModel
import com.example.groove.viewmodel.PlayerViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


private const val ALBUM_TITLE = ""

class AlbumSongsFragment : Fragment(R.layout.fragment_album_songs) {

    private var albumTitle: String? = null

    private lateinit var binding: FragmentAlbumSongsBinding

    private lateinit var mainSongViewModel: MainSongViewModel

    private lateinit var albumSongsAdapter: SongAdapter
    private lateinit var playerViewModel : PlayerViewModel

    private var albumSongs : ArrayList<Song>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            albumTitle = it.getString(ALBUM_TITLE)
            Log.d("ALBUM", albumTitle.toString())
        }



//        activity?.supportFragmentManager?.addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener {  })
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentAlbumSongsBinding.inflate(inflater, container, false)

        mainSongViewModel = (activity as MainActivity).mainSongViewModel
        playerViewModel = (activity as MainActivity).playerViewModel


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()
        setAlbumSongs()

        setHeaderDetails()

        onAlbumSongItemClick()
    }

    private fun setHeaderDetails() {
        Glide.with(this)
            .load(albumSongs?.get(0)?.artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_song_cover).centerInside())
            .into(binding.imgAlbum)

        binding.collapsingToolbar.title = albumTitle
    }



    private fun prepareRecyclerView() {
        albumSongsAdapter = SongAdapter(requireContext())
        binding.rvAlbumSongs.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = albumSongsAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            Log.d("ALBUM", "Prepared Recycler View")
        }
    }

    private fun setAlbumSongs() {
        albumSongs = mainSongViewModel.observeAlbumHashMapLiveData().value!![albumTitle]
        albumSongsAdapter.differ.submitList(albumSongs)

        Log.d("ALBUM", ALBUM_TITLE)
        Log.d("ALBUM", "Submitted Album Songs List")
        Log.d("ALBUM", albumSongs.toString())
    }


    private fun onAlbumSongItemClick() {
        albumSongsAdapter.onItemClick = { song, playlist, position ->
            playerViewModel.currentPlaylist.value = playlist
            playerViewModel.currentPosition.value = position

        }
    }


    companion object {
        @JvmStatic fun newInstance(param1: String) =
            AlbumSongsFragment().apply {
                arguments = Bundle().apply {
                    putString(ALBUM_TITLE, param1)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.btm_nav)
        navBar.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()

        requireActivity().findViewById<BottomNavigationView>(R.id.btm_nav)?.let{ navBar ->
            if(navBar.visibility == View.VISIBLE){
                navBar.visibility = View.GONE
            }
        }
    }

}