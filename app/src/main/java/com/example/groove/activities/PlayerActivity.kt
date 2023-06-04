package com.example.groove.activities

import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.groove.R
import com.example.groove.databinding.ActivityPlayerBinding


class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(
            R.anim.slide_up, R.anim.fade_out)


    }



    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.fade_in, R.anim.slide_down)
    }
}