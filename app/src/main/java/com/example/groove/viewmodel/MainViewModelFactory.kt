package com.example.groove.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.groove.repository.SongRepository

class MainViewModelFactory(
    private val songRepository: SongRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(songRepository, application) as T
    }

}