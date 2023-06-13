package com.example.groove.util

import com.example.groove.viewmodel.MainSongViewModel
import java.util.concurrent.TimeUnit

class utility {

    private lateinit var mainSongViewModel: MainSongViewModel

    companion object{
        fun formatDuration(duration: Long):String{
            val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
            val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                    minutes* TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
            return String.format("%02d:%02d", minutes, seconds)
        }
    }



}