package com.example.groove.activities

import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groove.R
import com.example.groove.databinding.ActivityPlayerBinding
import com.example.groove.util.OnSwipeTouchListener


class PlayerActivity : AppCompatActivity() {

    lateinit var binding: ActivityPlayerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSwipeGestures()

        overridePendingTransition(
            R.anim.slide_up, R.anim.fade_out
        )
    }

    private fun setSwipeGestures() {
        binding.root.setOnTouchListener(object : OnSwipeTouchListener(this) {
//            override fun onSwipeLeft() {
//                super.onSwipeLeft()
//                Toast.makeText(
//                    this@PlayerActivity, "Swipe Left gesture detected",
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//            }
//
//            override fun onSwipeRight() {
//                super.onSwipeRight()
//                Toast.makeText(
//                    this@PlayerActivity,
//                    "Swipe Right gesture detected",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onSwipeUp() {
//                super.onSwipeUp()
//                Toast.makeText(this@PlayerActivity, "Swipe up gesture detected", Toast.LENGTH_SHORT)
//                    .show()
//            }

            override fun onSwipeDown() {
                super.onSwipeDown()
//                Toast.makeText(this@PlayerActivity, "Swipe down gesture detected", Toast.LENGTH_SHORT)
//                    .show()
                onBackPressed()
            }
        })
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.fade_in, R.anim.slide_down
        )
    }
}