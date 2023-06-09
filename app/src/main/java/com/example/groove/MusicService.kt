package com.example.groove

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.groove.activities.MainActivity
import com.example.groove.db.SongDatabase
import com.example.groove.repository.SongRepository
import com.example.groove.viewmodel.MainViewModelFactory
import com.example.groove.viewmodel.PlayerViewModel
import com.example.groove.viewmodel.PlayerViewModelFactory
import java.lang.Exception

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null



    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    private var currentSongLink : String = ""

    override fun onBind(intent: Intent?): IBinder {
        Log.d("PLAYBACK", "@MusicService - onBind Called")
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnCompletionListener(this)
        mediaPlayer!!.setOnPreparedListener(this)
//        mediaPlayer!!.setOnSeekCompleteListener(this)
//        mediaPlayer!!.setOnInfoListener(this)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentSongLink = intent!!.getStringExtra("AudioLink").toString()

        mediaPlayer!!.reset()

        if (!mediaPlayer!!.isPlaying) {
            try {
                mediaPlayer!!.setDataSource(currentSongLink)
                mediaPlayer!!.prepareAsync()
            }catch(e: Exception){
                Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_LONG).show()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }

    fun pauseSong(){
        mediaPlayer!!.pause()
    }

    fun playSong(){
        mediaPlayer!!.start()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange <= 0){
            //pause music
            mediaPlayer!!.pause()

        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if(mp!!.isPlaying){
            mp.stop()
            mp.release()
        }

        stopSelf()
    }


//    fun seekBarSetup(){
//        runnable = Runnable {
//            MainActivity.binding = formatDuration(mediaPlayer!!.currentPosition.toLong())
//            MainActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
//            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
//        }
//        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
//    }

    override fun onPrepared(mp: MediaPlayer?) {
        playSong()
    }
//
//    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSeekComplete(mp: MediaPlayer?) {
//        TODO("Not yet implemented")
//    }
}