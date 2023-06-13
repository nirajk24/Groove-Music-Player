package com.example.groove.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.groove.model.Song
import com.example.groove.util.Constant
import com.example.groove.util.utility

class MainSongViewModel : ViewModel() {

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

    var sortingOrderLiveData = MutableLiveData<Boolean>(true)  // Asc = true, Des = false
    var sortByLiveData = MutableLiveData<String>(Constant.DATE)  // Title, Date, Album, Artist


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
        Log.d("ALBUM", "@MainSongViewModel AlbumHashMap".plus(albumHashMapLiveData.value.toString()))

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


    fun sortSongs(songList : MutableList<Song>) : MutableList<Song>{
        if(sortingOrderLiveData.value == true){
            Log.d("SORT", "Inside if statement")

            when(sortByLiveData.value){
                Constant.TITLE -> songList.sortBy { it.title.lowercase() }
                Constant.DATE -> songList.sortBy { it.dateAdded }
                Constant.ALBUM -> songList.sortBy { it.album.lowercase() }
                Constant.ARTIST -> songList.sortBy { it.artist.lowercase() }

            }
        } else {
            Log.d("SORT", "Inside else statement")

            when(sortByLiveData.value){
                Constant.TITLE -> songList.sortByDescending { it.title.lowercase() }
                Constant.DATE -> songList.sortByDescending { it.dateAdded }
                Constant.ALBUM -> songList.sortByDescending { it.album.lowercase() }
                Constant.ARTIST -> songList.sortByDescending { it.artist.lowercase() }
            }
        }

        return songList

    }








}