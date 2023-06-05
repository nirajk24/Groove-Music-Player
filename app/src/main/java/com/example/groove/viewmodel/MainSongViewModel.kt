package com.example.groove.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.groove.model.Song

class MainSongViewModel(val mainViewModel : MainViewModel) : ViewModel() {

    private lateinit var allSongsList : List<Song>

    fun setUpAllSongData(allSongList : List<Song>){
        allSongsList = allSongList
    }

    private var albumHashMap = HashMap<String, ArrayList<Song>>()

    private var albumHashMapLiveData = MutableLiveData<HashMap<String, ArrayList<Song>>>()

    fun observeAlbumHashMapLiveData() : LiveData<HashMap<String, ArrayList<Song>>> {
        Log.d("CHECK", "OBSERVED")
        return albumHashMapLiveData
    }

    private var artistHashMap = HashMap<String, ArrayList<Song>>()

    fun getArtistHashMap() : HashMap<String, ArrayList<Song>>{
        return artistHashMap
    }

    fun setUpAlbumHashMap(){
        if(allSongsList.isNotEmpty()){
            Log.d("CHECK", "SONG LIST NOT EMPTY")
            for (song in allSongsList){
                if(albumHashMap.containsKey(song.album)){
                    albumHashMap[song.album]?.add(song)
                }else{
                    albumHashMap[song.album] = ArrayList<Song>()
                    albumHashMap[song.album]?.add(song)
                }
            }
        }
        albumHashMapLiveData.value = albumHashMap
        Log.d("CHECK", albumHashMapLiveData.value.toString())
    }

    fun setUpArtistHashMap(){
        for (song in allSongsList){
            if(artistHashMap.containsKey(song.artist)){
                artistHashMap[song.artist]?.add(song)
            }else{
                artistHashMap[song.artist] = ArrayList<Song>()
                artistHashMap[song.artist]?.add(song)
            }
        }
    }





}