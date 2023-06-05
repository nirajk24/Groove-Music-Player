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

    private var albumHashMapLiveData = MutableLiveData<HashMap<String, ArrayList<Song>>>()
    fun observeAlbumHashMapLiveData() : LiveData<HashMap<String, ArrayList<Song>>> {
        return albumHashMapLiveData
    }

    private var artistHashMapLiveData = MutableLiveData<HashMap<String, ArrayList<Song>>>()
    fun observeArtistHashMapLiveData() : LiveData<HashMap<String, ArrayList<Song>>> {
        return artistHashMapLiveData
    }


    fun setUpAlbumHashMapLiveData(){
        val albumHashMap = HashMap<String, ArrayList<Song>>()
        if(allSongsList.isNotEmpty()){
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
        Log.d("CHECK", "@MainSongViewModel AlbumHashMap".plus(albumHashMapLiveData.value.toString()))

    }

    fun setUpArtistHashMapLiveData(){
        val artistHashMap = HashMap<String, ArrayList<Song>>()
        if(allSongsList.isNotEmpty()) {
            for (song in allSongsList) {
                if (artistHashMap.containsKey(song.artist)) {
                    artistHashMap[song.artist]?.add(song)
                } else {
                    artistHashMap[song.artist] = ArrayList<Song>()
                    artistHashMap[song.artist]?.add(song)
                }
            }
        }
        artistHashMapLiveData.value = artistHashMap
        Log.d("CHECK", "@MainSongViewModel ArtistHashMap".plus(albumHashMapLiveData.value.toString()))
    }





}