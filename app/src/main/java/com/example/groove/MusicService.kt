//package com.example.groove
//
//import android.R
//import android.app.Service
//import android.content.Intent
//import android.media.MediaPlayer
//import android.os.IBinder
//import android.widget.Toast
//import com.example.groove.model.Song
//import java.util.concurrent.TimeUnit
//
//
class MusicService
//    : Service()
{
//
//    private var mediaPlayer: MediaPlayer? = null
//
//    private lateinit var currentSong : Song
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//
//        createMediaPlayer()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Toast.makeText(this, "Music Service started by user.", Toast.LENGTH_LONG).show()
//        mediaPlayer!!.start()
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer!!.stop()
//        Toast.makeText(this, "Music Service destroyed by user.", Toast.LENGTH_LONG).show()
//    }
//
//    private fun formatDuration(duration: Long):String{
//        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
//        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
//                minutes* TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
//        return String.format("%02d:%02d", minutes, seconds)
//    }
//
//    fun createMediaPlayer(){
//        try {
//            // Checking and initialising Media Player
//            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
//
//            mediaPlayer!!.apply {
//                reset()
//                setDataSource(playingList[currentSongPosition].path)
//                prepare()
//            }
//
//        } catch (e: Exception) {
//            return
//        }
//    }
//
//    fun initializePlayingList(song: Song) {
//        currentSong = song
//    }
//
}
//
//
////private var myBinder = MyBinder()
////var mediaPlayer: MediaPlayer? = null
////private lateinit var mediaSession : MediaSessionCompat
////private lateinit var runnable: Runnable
////lateinit var audioManager: AudioManager
////
////// Activities reference
////val playerActivity = PlayerActivity()
////val mainActivity = MainActivity()
////
////
////private lateinit var playingList : List<Song>
////private var currentSongPosition = -1
////
////override fun onBind(intent: Intent?): IBinder {
////    return myBinder
////}
////
////inner class MyBinder: Binder(){
////    fun currentService(): MusicService{
////        return this@MusicService
////    }
////}
////
////fun initializePlayingList(currentPlayingList : List<Song>, currentSong : Int){
////    playingList = currentPlayingList
////    currentSongPosition = currentSong
////}
////
////fun createMediaPlayer(){
////    try{
////        // Checking and initialising Media Player
////        if(mediaPlayer == null) mediaPlayer = MediaPlayer()
////
////        mediaPlayer!!.apply {
////            reset()
////            setDataSource(playingList[currentSongPosition].path)
////            prepare()
////        }
////
////        updatePlayerActivityLayout()
////    }catch(e : Exception){return}
////}
////
////private fun updatePlayerActivityLayout() {
////    playerActivity.binding.apply {
////        playPauseButton.setImageResource(R.drawable.ic_pause)
////        tvCurrentSongProgress.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
////        tvCurrentSongTotalTime.text = formatDuration(mediaPlayer!!.duration.toLong())
////        playerSeekbar.progress = 0
////        playerSeekbar.max = mediaPlayer!!.duration
////    }
////}
////
////fun seekBarSetup(){
////    runnable = Runnable {
////        playerActivity.binding.tvCurrentSongProgress.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
////        playerActivity.binding.playerSeekbar.progress = mediaPlayer!!.currentPosition
////        Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
////    }
////    Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
////}