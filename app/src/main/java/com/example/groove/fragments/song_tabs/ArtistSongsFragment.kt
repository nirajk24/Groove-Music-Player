package com.example.groove.fragments.song_tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.adapter.SongAdapter
import com.example.groove.databinding.FragmentArtistSongsBinding
import com.example.groove.model.Song
import com.example.groove.viewmodel.MainSongViewModel
import com.example.groove.viewmodel.PlayerViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val ARTIST_TITLE = ""

class ArtistSongsFragment : Fragment(R.layout.fragment_artist_songs) {

    private var artistTitle: String? = null

    private lateinit var binding : FragmentArtistSongsBinding

    private lateinit var mainSongViewModel: MainSongViewModel

    private lateinit var artistSongsAdapter: SongAdapter
    private lateinit var playerViewModel : PlayerViewModel

    private var artistSongs : ArrayList<Song>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            artistTitle = it.getString(ARTIST_TITLE)
            Log.d("ALBUM", artistTitle.toString())


            val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.btm_nav)
            navBar.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistSongsBinding.inflate(inflater, container, false)

        mainSongViewModel = (activity as MainActivity).mainSongViewModel
        playerViewModel = (activity as MainActivity).playerViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()
        setArtistSongs()

        onArtistSongItemClick()
    }

    private fun prepareRecyclerView() {
        artistSongsAdapter = SongAdapter()
        binding.rvArtistSongs.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = artistSongsAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            Log.d("ALBUM", "Prepared Recycler View")
        }
    }

    private fun setArtistSongs() {
        artistSongs = mainSongViewModel.observeArtistHashMapLiveData().value!![artistTitle]
        artistSongsAdapter.differ.submitList(artistSongs)

    }

    private fun onArtistSongItemClick() {
        artistSongsAdapter.onItemClick = { song, playlist, position ->
            playerViewModel.currentPlaylist.value = playlist
            playerViewModel.currentPosition.value = position
//

        }
    }


    companion object {

        @JvmStatic fun newInstance(param1: String) =
            ArtistSongsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARTIST_TITLE, param1)
                }
            }
    }

    override fun onPause() {
        super.onPause()

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.btm_nav)
        navBar.visibility = View.VISIBLE
    }


}