package com.example.groove.activities

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.groove.R
import com.example.groove.databinding.ActivityMainBinding
import com.example.groove.db.SongDatabase
import com.example.groove.repository.SongRepository
import com.example.groove.viewmodel.MainViewModel
import com.example.groove.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController

    val mainViewModel : MainViewModel by lazy {
        val songRepository = SongRepository(SongDatabase.getInstance(this))
        val mainViewModelFactory = MainViewModelFactory(songRepository, application)
        ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestRuntimePermission()
        setUpBottomNavigation()
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

    private fun requestRuntimePermission() :Boolean{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
                return false
            }
        }
        else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO), 13)
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    mainViewModel.scanForSongs()
                }
            }
            else
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
        }
    }



}