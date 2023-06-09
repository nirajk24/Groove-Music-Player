package com.example.groove.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.groove.MusicService
import com.example.groove.model.Song

class PlayerViewModel(
    val mainSongViewModel: MainSongViewModel,
    private val application: Application,
) : AndroidViewModel(application){


    // Live Data
    var IS_PLAYING = MutableLiveData<Boolean>(false)

    var CURRENT_PLAYLIST = MutableLiveData<List<Song>>()

    var CURRENT_SONG = MutableLiveData<Song>()

    fun setCurrentPlaylist(playlist: List<Song>){
        CURRENT_PLAYLIST.value = playlist
    }

    var CURRENT_POSITION = MutableLiveData<Int>()
        get() = field

    var IS_SHUFFLE = MutableLiveData<Boolean>(false)

    var IS_LOOP = MutableLiveData<Boolean>(false)

    var IS_REPEAT = MutableLiveData<Boolean>(false)



}