package com.example.groove.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.groove.model.Song

class PlayerViewModel(
    val mainSongViewModel: MainSongViewModel,
    private val application: Application,
) : AndroidViewModel(application){


    // Live Data
    var isPlaying = MutableLiveData<Boolean>(false)

    var currentPlaylist = MutableLiveData<List<Song>>()

    var currentSong = MutableLiveData<Song>()

    var currentPosition = MutableLiveData<Int>()
        get() = field

    var IS_SHUFFLE = MutableLiveData<Boolean>(false)

    var IS_LOOP = MutableLiveData<Boolean>(false)

    var IS_REPEAT = MutableLiveData<Boolean>(false)



}