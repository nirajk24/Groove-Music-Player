package com.example.groove.activities

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.example.groove.R
import com.example.groove.databinding.ActivityMainBinding
import com.example.groove.db.SongDatabase
import com.example.groove.repository.SongRepository
import com.example.groove.viewmodel.MainSongViewModel
import com.example.groove.viewmodel.MainSongViewModelFactory
import com.example.groove.viewmodel.MainViewModel
import com.example.groove.viewmodel.MainViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var playerBottomSheetBehavior : BottomSheetBehavior<View>


    val mainViewModel: MainViewModel by lazy {
        val songRepository = SongRepository(SongDatabase.getInstance(this))
        val mainViewModelFactory = MainViewModelFactory(songRepository, application)
        ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]
    }

    val mainSongViewModel: MainSongViewModel by lazy {
        val mainSongViewModelFactory = MainSongViewModelFactory(mainViewModel)
        ViewModelProvider(this, mainSongViewModelFactory)[MainSongViewModel::class.java]
    }


    // Main
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // If a user deny a permission 2 times then it's permanent denied
        // (only way to enable now is through app settings)
        requestRuntimePermission()
        setUpBottomNavigation()

        setUpBottomPlayerLayout()

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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            mainViewModel.scanForSongs()
        }
    }


    private fun setUpBottomPlayerLayout() {
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
                        binding.bigPlayerLayout.root.visibility = View.GONE
                        binding.miniPlayerLayout.root.visibility = View.VISIBLE




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
                        if(binding.btmNav.isVisible && binding.miniPlayerLayout.root.isVisible){
                            animateBottomNav()
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
                        if(!binding.btmNav.isVisible && binding.bigPlayerLayout.root.isVisible){
                            animateBottomNav()
                        }
                        else {
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
        binding.miniPlayerLayout.root.visibility = View.GONE
        binding.bigPlayerLayout.root.visibility = View.VISIBLE
    }

    private fun animateBottomNav() {
        if (!binding.btmNav.isVisible) {
            val animate =
                TranslateAnimation(0f, 0f, binding.btmNav.height.toFloat(), 0f)
            animate.duration = 200;
            binding.btmNav.startAnimation(animate);
            binding.btmNav.visibility = View.VISIBLE;
        }else
            if (binding.btmNav.isVisible) {
                val animate =
                    TranslateAnimation(0f, 0f, 0f, binding.btmNav.height.toFloat())
                animate.duration = 300;
                binding.btmNav.startAnimation(animate);
                binding.btmNav.visibility = View.GONE;
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if(playerBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
            animateBottomNav()
            playerBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }else{
            super.onBackPressed()
        }
    }



}