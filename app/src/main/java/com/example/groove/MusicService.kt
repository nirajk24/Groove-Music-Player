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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.util.TimeUtils.formatDuration
import com.example.groove.activities.MainActivity
import com.example.groove.activities.PlayerActivity
import com.example.groove.model.Song
import java.util.concurrent.TimeUnit

class MusicService : Service() {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    // Activities reference
    val playerActivity = PlayerActivity()
    val mainActivity = MainActivity()


    private lateinit var playingList : List<Song>
    private var currentSongPosition = -1

    override fun onBind(intent: Intent?): IBinder {
        return myBinder
    }

    inner class MyBinder: Binder(){
        fun currentService(): MusicService{
            return this@MusicService
        }
    }

    fun initializePlayingList(currentPlayingList : List<Song>, currentSong : Int){
        playingList = currentPlayingList
        currentSongPosition = currentSong
    }

    fun createMediaPlayer(){
        try{
            // Checking and initialising Media Player
            if(mediaPlayer == null) mediaPlayer = MediaPlayer()

            mediaPlayer!!.apply {
                reset()
                setDataSource(playingList[currentSongPosition].path)
                prepare()
            }

            updatePlayerActivityLayout()
        }catch(e : Exception){return}
    }

    private fun updatePlayerActivityLayout() {
        playerActivity.binding.apply {
            playPauseButton.setImageResource(R.drawable.ic_pause)
            tvCurrentSongProgress.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            tvCurrentSongTotalTime.text = formatDuration(mediaPlayer!!.duration.toLong())
            playerSeekbar.progress = 0
            playerSeekbar.max = mediaPlayer!!.duration
        }
    }

    fun seekBarSetup(){
        runnable = Runnable {
            playerActivity.binding.tvCurrentSongProgress.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            playerActivity.binding.playerSeekbar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    private fun formatDuration(duration: Long):String{
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes* TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}