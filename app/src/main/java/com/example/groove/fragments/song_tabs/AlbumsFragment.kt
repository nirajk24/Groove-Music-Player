package com.example.groove.fragments.song_tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.adapter.AlbumAdapter
import com.example.groove.databinding.FragmentAlbumsBinding
import com.example.groove.viewmodel.MainSongViewModel
import com.example.groove.viewmodel.PlayerViewModel

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


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareAlbumRecyclerView()
        observeAlbumHashMapLiveData()

        onAlbumItemClick()

    }


    private fun onAlbumItemClick() {
        albumAdapter.onItemClick = { albumTitle ->
            Log.d("ALBUM", "Passing Album ".plus(albumTitle))
            val mFrag: Fragment = AlbumSongsFragment.newInstance(albumTitle)
            replaceFragment(mFrag)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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