package com.example.groove.fragments.song_tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.adapter.SongAdapter
import com.example.groove.databinding.FragmentSongsBinding
import com.example.groove.model.Song
import com.example.groove.repository.PrefRepository
import com.example.groove.util.Constant
import com.example.groove.util.Constant.Companion.ALBUM
import com.example.groove.util.Constant.Companion.ARTIST
import com.example.groove.util.Constant.Companion.DATE
import com.example.groove.util.Constant.Companion.TITLE
import com.example.groove.viewmodel.MainSongViewModel
import com.example.groove.viewmodel.MainViewModel
import com.example.groove.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch


class SongsFragment : Fragment(R.layout.fragment_songs) {

    private lateinit var binding: FragmentSongsBinding
    private lateinit var mainViewModel: MainViewModel

    private lateinit var mainSongViewModel: MainSongViewModel
    private lateinit var playerViewModel: PlayerViewModel

    // Adapter
    private lateinit var songAdapter: SongAdapter

    // Song List
    private lateinit var allSongListInOrder: MutableList<Song>

    private lateinit var prefRepository: PrefRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = (activity as MainActivity).mainViewModel
        mainSongViewModel = (activity as MainActivity).mainSongViewModel
        playerViewModel = (activity as MainActivity).playerViewModel

        prefRepository = PrefRepository(requireContext())


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


        sortByRadioButtons()
        sortOrderRadioButton()

        setSortingLayout()
        setSorting()

    }



    private fun prepareSongsRecyclerView() {
        songAdapter = SongAdapter(requireContext())
        binding.rvSongs.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = songAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
    }

    private fun observeSongs() {
        mainViewModel.observeAllSongsLiveData()
            .observe(viewLifecycleOwner, Observer { songListRandom ->

                allSongListInOrder = songListRandom.values.toMutableList()

                if (allSongListInOrder.isNotEmpty()) {
                    binding.tvNoSongs.visibility = View.GONE  // -> Hiding No Songs Text
                    binding.tvSongsNumber.text = allSongListInOrder.size.toString().plus(" Songs")
                }

                sortAndSetData()

            })

        mainSongViewModel.sortByLiveData.observe(viewLifecycleOwner, Observer {
            sortAndSetData()
        })

        mainSongViewModel.sortingOrderLiveData.observe(viewLifecycleOwner, Observer {
            sortAndSetData()
        })
    }

    private fun sortAndSetData() {
        allSongListInOrder = mainSongViewModel.sortSongs(allSongListInOrder)

        songAdapter.differ.submitList(allSongListInOrder)
        songAdapter.notifyDataSetChanged()

        lifecycleScope.launch {
            // Initialising Data for Songs, Artist and Album
            mainSongViewModel.setUpAllSongData(allSongListInOrder)
            mainSongViewModel.setUpAlbumHashMapLiveData()
            mainSongViewModel.setUpArtistHashMapLiveData()
        }
    }

    private fun onSongItemClick() {
        songAdapter.onItemClick = { song, playlist, position ->

            playerViewModel.currentPlaylist.value = playlist
            playerViewModel.currentPosition.value = position

        }

    }


    // <----- Sorting Starts ----->
    private fun setSortingLayout() {
        binding.btnSort.setOnClickListener {
            if (binding.sortingLayout.isVisible) {
                binding.sortingLayout.visibility = View.GONE
                binding.btnSort.setImageResource(R.drawable.ic_sort)
            } else if (!(binding.sortingLayout.isVisible)) {
                binding.sortingLayout.visibility = View.VISIBLE
                binding.btnSort.setImageResource(R.drawable.ic_cross)
            }
        }
    }

    private fun setSorting() {
        mainSongViewModel.sortByLiveData.value = prefRepository.getSortBy()
        mainSongViewModel.sortingOrderLiveData.value = prefRepository.getSortingOrder()

        if (prefRepository.getSortingOrder())
            binding.radioSortOrderAscending.toggle()
        else binding.radioSortOrderDescending.toggle()

        Log.d("SORTING", prefRepository.getSortBy())
        Log.d("SORTING", prefRepository.getSortingOrder().toString())
        when (prefRepository.getSortBy()) {
            TITLE -> binding.radioSortByTitle.toggle()
            DATE -> binding.radioSortByDate.toggle()
            ALBUM -> binding.radioSortByAlbum.toggle()
            ARTIST -> binding.radioSortByArtist.toggle()
        }
    }

    private fun sortByRadioButtons() {
        binding.radioSortBy.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.radioSortByTitle -> {
                    mainSongViewModel.sortByLiveData.value = Constant.TITLE
                    prefRepository.setSortBy(TITLE)
                }

                R.id.radioSortByDate -> {
                    mainSongViewModel.sortByLiveData.value = Constant.DATE
                    prefRepository.setSortBy(DATE)
                }

                R.id.radioSortByAlbum -> {
                    mainSongViewModel.sortByLiveData.value = Constant.ALBUM
                    prefRepository.setSortBy(ALBUM)
                }

                R.id.radioSortByArtist -> {
                    mainSongViewModel.sortByLiveData.value = Constant.ARTIST
                    prefRepository.setSortBy(ARTIST)
                }
            }
        }
    }

    private fun sortOrderRadioButton() {
        binding.radioSortOrder.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.radioSortOrderAscending -> {
                    mainSongViewModel.sortingOrderLiveData.value = true
                    prefRepository.setSortingOrder(true)
                }

                R.id.radioSortOrderDescending -> {
                    mainSongViewModel.sortingOrderLiveData.value = false
                    prefRepository.setSortingOrder(false)
                }
            }
        }
    }


    // <----- Sorting Ends ----->

}