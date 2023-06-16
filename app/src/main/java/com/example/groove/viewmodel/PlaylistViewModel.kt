package com.example.groove.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.groove.model.Playlist
import com.example.groove.model.Song

class PlaylistViewModel : ViewModel() {

    private var allPlaylistSongsLiveDataMap = MutableLiveData<HashMap<String, List<Song>>>()
    fun observeAllPlaylistsSongsLiveData(): LiveData<HashMap<String, List<Song>>> {
        return allPlaylistSongsLiveDataMap
    }

    fun setPlaylistSongs(playlists : List<Playlist>, allSongsMap : HashMap<String, Song>) {
        val allPlaylist: List<Playlist> = playlists
        val tempHashMap = HashMap<String, List<Song>>()
        if (allPlaylist.isNotEmpty()) {
            for (playlist in allPlaylist) {
                val songList = mutableListOf<Song>()

                for (songTitle in playlist.getSongTitles()) {
//                    allSongsLiveDataMap.value?.get(songTitle)?.let { song ->
//                        songList.add(song)
//                    }
                    if (allSongsMap.keys.contains(songTitle)) {
                        allSongsMap[songTitle]?.let { songList.add(it) }
                    }else {

                    }
                }

                tempHashMap[playlist.title] = songList
            }
            allPlaylistSongsLiveDataMap.value = tempHashMap
        }
    }
}