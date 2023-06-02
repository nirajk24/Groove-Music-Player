package com.example.groove.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.groove.R
import com.example.groove.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}