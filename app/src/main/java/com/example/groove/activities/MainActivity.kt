package com.example.groove.activities

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.groove.application.ApplicationClass.Companion.CHANNEL_ID_2
import com.example.groove.application.ApplicationClass.Companion.PAUSE
import com.example.groove.application.ApplicationClass.Companion.PLAY
import com.example.groove.application.ApplicationClass.Companion.PREVIOUS
import com.example.groove.service.MusicService
import com.example.groove.notification.NotificationReceiver
import com.example.groove.R
import com.example.groove.databinding.ActivityMainBinding
import com.example.groove.db.PlaylistDatabase
import com.example.groove.repository.PlaylistRepository
import com.example.groove.repository.SongRepository
import com.example.groove.util.utility
import com.example.groove.viewmodel.MainSongViewModel
import com.example.groove.viewmodel.MainSongViewModelFactory
import com.example.groove.viewmodel.MainViewModel
import com.example.groove.viewmodel.MainViewModelFactory
import com.example.groove.viewmodel.PlayerViewModel
import com.example.groove.viewmodel.PlayerViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), ServiceConnection {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var playerBottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var mediaSessionCompat: MediaSessionCompat


    // <----- Service Starts ---->
    private lateinit var serviceIntent: Intent
    private var musicService: MusicService? = null
    // <----- Service Ends ---->


    // <----- ViewModel Starts ---->
    val mainViewModel: MainViewModel by lazy {
        val songRepository = SongRepository(PlaylistDatabase.getInstance(this))
        val playlistRepository = PlaylistRepository(PlaylistDatabase.getInstance(this))
        val mainViewModelFactory = MainViewModelFactory(playlistRepository, application)
        ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]
    }

    val mainSongViewModel: MainSongViewModel by lazy {
        val mainSongViewModelFactory = MainSongViewModelFactory(mainViewModel)
        ViewModelProvider(this, mainSongViewModelFactory)[MainSongViewModel::class.java]
    }

    val playerViewModel: PlayerViewModel by lazy {
        val playerViewModelFactory = PlayerViewModelFactory(mainSongViewModel, application)
        ViewModelProvider(this, playerViewModelFactory)[PlayerViewModel::class.java]
    }
    // <----- ViewModel Ends ---->


    // <----- MAIN ---->
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // If a user deny a permission 2 times then it's permanent denied
        // (only way to enable now is through app settings)
        requestRuntimePermission()

        setUpBottomNavigation()

        mediaSessionCompat = MediaSessionCompat(baseContext, "My Audio")

        serviceIntent = Intent(this, MusicService::class.java)

        initiateBottomPlayerLayout()
        setUpBottomPlayerLayout()
        manageBottomNavigation()

        clickListener()




        playCurrentPlaylist()

        updateCurrentSong()
//        setPlayerLayout()


    }



    // <----- Service Starts ---->
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("PLAYBACK", "@PlayerActivity onServiceConnected Called")
        if (musicService == null) {
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
            musicService!!.audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(
                musicService,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }

        seekBarListener()
        initialiseSeekbar()

        musicService!!.showNotification(R.drawable.ic_pause) /// ---> FIX

        musicService!!.mediaPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener() {

            Log.d("PLAYBACK", "On Completed Listened")
            setNextSong()
        })


    }

    override fun onServiceDisconnected(name: ComponentName?) {
        if (musicService != null)
            musicService!!.stopSelf()
    }

    private fun playCurrentPlaylist() {
        playerViewModel.currentSong.observeForever(Observer {
            playAudio(playerViewModel.currentSong.value!!.path)


            musicService?.showNotification(R.drawable.ic_pause)

            Log.d("PLAYBACK", "currentSong changed - will call initialise seekbar")
            initialiseSeekbar()

        })
    }


    private fun playAudio(link: String) {
        stopService(serviceIntent)
        serviceIntent.putExtra("AudioLink", link)

        if (musicService != null) {    /// -----> FIX
            musicService!!.showNotification(R.drawable.ic_pause)
        }
        try {
            startService(serviceIntent)
            playerViewModel.isPlaying.value = true
            bindService(serviceIntent, this, BIND_AUTO_CREATE)


        } catch (e: SecurityException) {
            Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCurrentSong() {
//        playerViewModel.currentPlaylist.observe(this, Observer {
//            playerViewModel.currentSong.value = playerViewModel.currentPlaylist.value
//                ?.get(playerViewModel.currentPosition.value!!)
//        })

        playerViewModel.currentPosition.observeForever(Observer {
            Log.d("PLAYBACK", "Observed Current Song")

            playerViewModel.currentSong.value = playerViewModel.currentPlaylist.value
                ?.get(playerViewModel.currentPosition.value!!)
        })
    }

    private fun seekBarListener() {
        binding.bigPlayerLayout.playerSeekbar
            .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                    if (fromUser) {
                        val songProgressTextView = binding.bigPlayerLayout.tvCurrentSongProgress
                        songProgressTextView.text = utility.formatDuration(progress.toLong())

                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    musicService!!.mediaPlayer!!.seekTo(seekBar!!.progress)
                }
            })
    }


    private fun initialiseSeekbar() {
        if (musicService != null) {
            Log.d("PLAYBACK", "@PlayerActivity UNDER initialise Seekbar")
            val seekbar = binding.bigPlayerLayout.playerSeekbar
            val songProgressTextView = binding.bigPlayerLayout.tvCurrentSongProgress
            val mediaPlayer = musicService!!.mediaPlayer!!
            seekbar.max = playerViewModel.currentSong.value!!.duration.toInt()

            val handler = Handler()
            handler.postDelayed(object : Runnable {

                override fun run() {
                    try {
                        Log.d("SEEKBAR", "@PlayerActivity UNDER run function")
                        seekbar.progress = mediaPlayer.currentPosition
                        songProgressTextView.text =
                            utility.formatDuration(seekbar.progress.toLong())
                        handler.postDelayed(this, 1000)
                    } catch (e: Exception) {
                        Log.d("SEEKBAR", "@PlayerActivity Exception ${e.message}")
                        seekbar.progress = 0
                    }
                }
            }, 0)
        }
    }

    // <----- Service Ends ---->


    // <------ Notification Starts ----->

    fun showNotification(playPauseBtn: Int) {
        val intent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val prevIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(PREVIOUS)
        val prevPending = PendingIntent
            .getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE)

        val playPauseIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(PAUSE)
        val playPausePending = PendingIntent
            .getBroadcast(this, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(PLAY)
        val nextPending = PendingIntent
            .getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)


        val imgArt = getImageArt(playerViewModel.currentSong.value!!.path)
        val thumb = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.music_icon)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_2)
            .setSmallIcon(playPauseBtn)
            .setLargeIcon(thumb)
            .setContentTitle(playerViewModel.currentSong.value!!.title)
            .setContentText(playerViewModel.currentSong.value!!.artist)
            .addAction(R.drawable.ic_previous, "Previous", prevPending)
            .addAction(playPauseBtn, "Pause", playPausePending)
            .addAction(R.drawable.ic_next, "Next", nextPending)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionCompat.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notification)
    }

    private fun getImageArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        return retriever.embeddedPicture
    }


    // <------ Notification Ends ----->


    // <----- View Starts ---->
    private fun clickListener() {
        // Mini Player
        binding.miniPlayerLayout.btnPlayPause.setOnClickListener { playPauseSong() }
        binding.miniPlayerLayout.btnNext.setOnClickListener { setNextSong() }

        // Big Player
        binding.bigPlayerLayout.btnPlayPause.setOnClickListener { playPauseSong() }
        binding.bigPlayerLayout.nextButton.setOnClickListener { setNextSong() }
        binding.bigPlayerLayout.previousButton.setOnClickListener { setPrevSong() }
    }


    private fun setNextSong() {
        playerViewModel.let {
            val currentPos = it.currentPosition.value
            val listSize = it.currentPlaylist.value!!.size
            if (currentPos != null) {
                if (currentPos < listSize - 1) {
                    playerViewModel.currentPosition.value =
                        playerViewModel.currentPosition.value!! + 1
                } else {
                    Toast.makeText(this, "No Next Song!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setPrevSong() {
        playerViewModel.let {
            val currentPos = it.currentPosition.value
//            var listSize = it.CURRENT_PLAYLIST.value!!.size
            if (currentPos != null) {
                if (currentPos != 0) {
                    playerViewModel.currentPosition.value =
                        playerViewModel.currentPosition.value!! - 1
                } else {
                    Toast.makeText(this, "No Previous Song!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun playPauseSong() {
        if (playerViewModel.isPlaying.value == true) {
            playerViewModel.isPlaying.value = false
            musicService!!.pauseSong()
            musicService?.showNotification(R.drawable.ic_play)

        } else {
            playerViewModel.isPlaying.value = true
            musicService!!.playSong()
            musicService?.showNotification(R.drawable.ic_pause)
        }
    }


    private fun setUpBottomPlayerLayout() {
        playerViewModel.currentSong.observe(this, Observer { song ->


            // Set mini Player
            binding.apply {

                playerBottomSheet.visibility = View.VISIBLE

                Glide.with(this@MainActivity).asBitmap()
                    .load(song.artUri)
                    .apply(RequestOptions().placeholder(R.drawable.ic_song_cover).centerInside())
                    .centerCrop()
                    .into(this.miniPlayerLayout.ivSongImage)
                miniPlayerLayout.tvSongTitle.text = song.title
                miniPlayerLayout.tvSongArtist.text = song.artist

                // Set Big Player
                Glide.with(this.root).asBitmap()
                    .load(song.artUri)
                    .apply(RequestOptions().placeholder(R.drawable.ic_song_cover).centerInside())
                    .centerCrop()
                    .into(this.bigPlayerLayout.imgCurrentSongImage)

                bigPlayerLayout.apply {
                    tvCurrentSongTitle.text = song.title
                    tvCurrentSongInfo.text = song.artist
                    tvCurrentSongProgress.text = "00:00"
                    tvCurrentSongTotalTime.text = utility.formatDuration(song.duration)
                }
            }
        })

        playerViewModel.isPlaying.observe(this, Observer { isPlaying ->
            if (isPlaying == true) {
                // MiniPlayer
                binding.miniPlayerLayout.btnPlayPause
                    .setImageDrawable(
                        AppCompatResources
                            .getDrawable(this, R.drawable.ic_pause)
                    )

                // BigPlayer
                binding.bigPlayerLayout.btnPlayPause
                    .setImageDrawable(
                        AppCompatResources
                            .getDrawable(this, R.drawable.ic_pause)
                    )
            } else {
                // MiniPlayer
                binding.miniPlayerLayout.btnPlayPause
                    .setImageDrawable(
                        AppCompatResources
                            .getDrawable(this, R.drawable.ic_play)
                    )

                // BigPlayer
                binding.bigPlayerLayout.btnPlayPause
                    .setImageDrawable(
                        AppCompatResources
                            .getDrawable(this, R.drawable.ic_play)
                    )
            }
        })

    }

    private fun setUpBottomNavigation() {
        // Setting up navigation through Bottom Navigation
        // 1 -> First find the navHost using the same method
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        // 2 -> Find navController for the navHostFragment
        navController = navHostFragment.navController
        // 3 -> Set up Nav controller with Bottom Navigation to enable Navigation
        NavigationUI.setupWithNavController(binding.btmNav, navController)
    }


    // <----- View Ends ---->


    // <----- Permission Starts ----->
    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestRuntimePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
                return false
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
                    13
                )
                return false
            }
        }
        lifecycleScope.launch {
            mainViewModel.scanForSongs()
            mainViewModel.setPlaylist()
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    mainViewModel.scanForSongs()
                    mainViewModel.setPlaylist()
                }
            } else
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                        13
//                    )
//                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
//                        13
//                    )
//                }
                requestRuntimePermission()
        }
    }

    // <----- Permission Ends ---->


    // <----- BottomPlayer Starts ---->
    private fun initiateBottomPlayerLayout() {
        playerBottomSheetBehavior = BottomSheetBehavior.from(binding.playerBottomSheet)

        // Slide Out (Down) Transition
        val transitionSlideOut: Transition = Slide(Gravity.BOTTOM)
        transitionSlideOut.duration = 2000;

        // Fade In Transition
        val transitionFadeIn: Transition = Fade(Fade.IN)
        transitionFadeIn.duration = 150;

        // Fade Out Transition
        val transitionFadeOut: Transition = Fade(Fade.OUT)
        transitionFadeOut.duration = 3000;

        binding.miniPlayerLayout.root.setOnClickListener {
            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            animateBottomNav()
            bringOutBigPlayer()

        }

//        binding.bigPlayerLayout.root.setOnClickListener {
//            binding.btmNav.visibility = View.GONE
//        }


        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
//                        binding.btmNav.animate().alpha(1.0f).duration = 100;

                        // Handling Player Layout
                        bringOutMiniPlayer()





                        transitionFadeIn.addTarget(binding.btmNav);
                        TransitionManager.beginDelayedTransition(binding.root, transitionFadeIn)
                        binding.btmNav.visibility = View.VISIBLE

//                        if (!binding.btmNav.isVisible) {
//                            val animate =
//                                TranslateAnimation(0f, 0f, binding.btmNav.height.toFloat(), 0f,)
//                            animate.duration = 200;
//                            binding.btmNav.startAnimation(animate);
//                            binding.btmNav.visibility = View.VISIBLE;
//                        }

                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        if (binding.btmNav.isVisible && binding.miniPlayerLayout.root.isVisible) {
//                            animateBottomNav()
//                            SlideView.slideView(binding.btmNav, binding.btmNav.layoutParams.height, 0)
                            bringOutBigPlayer()
                        }


//                        animateBottomNav()


//                        transitionSlideOut.addTarget(binding.btmNav);
//                        TransitionManager.beginDelayedTransition(binding.root, transitionSlideOut)
//                        binding.btmNav.visibility = View.GONE

//                        transitionFadeIn.removeTarget(binding.btmNav)
//                        transitionFadeIn.addTarget(binding.mainPlayerLayout)
//
//                        TransitionManager.beginDelayedTransition(binding.root, transitionFadeIn)


//                        binding.btmNav.visibility = View.GONE
//                        binding.btmNav.animate()
//                            .translationY(0f)
//                            .alpha(0.0f).duration = 500;
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.btmNav.visibility = View.GONE
                    }


                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }

                    BottomSheetBehavior.STATE_SETTLING -> {
                        if (!binding.btmNav.isVisible && binding.bigPlayerLayout.root.isVisible) {
                            animateBottomNav()
                        } else {
                            binding.btmNav.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        }

        playerBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun bringOutBigPlayer() {

        val transitionFadeOut: Transition = Fade(Fade.OUT)
        transitionFadeOut.duration = 150;
        transitionFadeOut.addTarget(binding.miniPlayerLayout.root);
        TransitionManager.beginDelayedTransition(binding.root, transitionFadeOut)
        binding.miniPlayerLayout.root.visibility = View.GONE

        // Fade In Transition
        val transitionFadeIn: Transition = Fade(Fade.IN)
        transitionFadeIn.duration = 150;
        transitionFadeIn.addTarget(binding.bigPlayerLayout.root);
        TransitionManager.beginDelayedTransition(binding.root, transitionFadeIn)
        binding.bigPlayerLayout.root.visibility = View.VISIBLE
    }

    private fun bringOutMiniPlayer() {
        // Fade In Transition
        val transitionFadeIn: Transition = Fade(Fade.IN)
        transitionFadeIn.duration = 100;
        transitionFadeIn.addTarget(binding.miniPlayerLayout.root);
        TransitionManager.beginDelayedTransition(binding.root, transitionFadeIn)
        binding.miniPlayerLayout.root.visibility = View.VISIBLE

        // Fade Out Transition
        val transitionFadeOut: Transition = Fade(Fade.OUT)
        transitionFadeOut.duration = 200;
        transitionFadeOut.addTarget(binding.bigPlayerLayout.root);
        TransitionManager.beginDelayedTransition(binding.root, transitionFadeOut)
        binding.bigPlayerLayout.root.visibility = View.GONE
    }

    private fun animateBottomNav() {
        if (!binding.btmNav.isVisible) {
            val animate =
                TranslateAnimation(0f, 0f, binding.btmNav.height.toFloat(), 0f)
            animate.duration = 200;
            binding.btmNav.startAnimation(animate);
            binding.btmNav.visibility = View.VISIBLE;
        } else
            if (binding.btmNav.isVisible) {
                val animate =
                    TranslateAnimation(0f, 0f, 0f, binding.btmNav.height.toFloat())
                animate.duration = 300;
                binding.btmNav.startAnimation(animate)
                binding.btmNav.visibility = View.GONE
            }
    }

    // <----- BottomPlayer Starts ---->

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (playerBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            animateBottomNav()
            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            mainViewModel.scanForSongs()
            mainViewModel.setPlaylist()
        }
    }



    private fun manageBottomNavigation() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.albumSongsFragment) {
                binding.btmNav.visibility = View.GONE
            } else {
                binding.btmNav.visibility = View.VISIBLE
            }
        }

    }

    fun showUpButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_container)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//
//
//    }
}