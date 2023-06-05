package com.example.groove.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainSongViewModelFactory(private val mainViewModel: MainViewModel)
    :ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainSongViewModel(mainViewModel) as T
    }


}