package com.example.groove.fragments.bottom_nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.transition.TransitionInflater
import com.example.groove.R
import com.example.groove.activities.MainActivity
import com.example.groove.adapter.AlbumAdapter
import com.example.groove.adapter.ArtistAdapter
import com.example.groove.adapter.SongViewPagerAdapter
import com.example.groove.databinding.FragmentMainSongsBinding
import com.example.groove.fragments.song_tabs.AlbumSongsFragment
import com.example.groove.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator


class MainSongsFragment : Fragment(R.layout.fragment_main_songs) {

    private lateinit var binding: FragmentMainSongsBinding
    private lateinit var mainViewModel: MainViewModel

    private val tabsArray = arrayOf("Songs", "Albums", "Artists")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Animation for entering
//        val inflater = TransitionInflater.from(requireContext())
//        enterTransition = inflater.inflateTransition(R.transition.fade)

        mainViewModel = (activity as MainActivity).mainViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainSongsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPagerAdapter = SongViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPagerAdapter

        // Connecting ViewPager to Tab Layout
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()

    }





}