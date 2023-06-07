package com.example.groove.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.groove.model.Song

class PlayerViewModel(
    val mainSongViewModel: MainSongViewModel,
    private val application: Application,
) : AndroidViewModel(application) {


    var IS_PLAYING = MutableLiveData<Boolean>(false)

    var CURRENT_PLAYLIST = MutableLiveData<List<Song>>()

    var CURRENT_SONG = MutableLiveData<Song>()

    fun setCurrentPlaylist(playlist: List<Song>){
        CURRENT_PLAYLIST.value = playlist
    }

    var CURRENT_POSITION = MutableLiveData<Int>(0)

    var IS_SHUFFLE = MutableLiveData<Boolean>(false)

    var IS_LOOP = MutableLiveData<Boolean>(false)

    var IS_REPEAT = MutableLiveData<Boolean>(false)








}