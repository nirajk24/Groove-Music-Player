package com.example.groove.repository

import com.example.groove.db.PlaylistDatabase
import com.example.groove.model.Playlist
import com.example.groove.model.Song

class PlaylistRepository(private val db: PlaylistDatabase) {

    suspend fun upsertPlaylist(playlist: Playlist) = db.getPlaylistDao().upsertPlaylist(playlist)
    suspend fun deletePlaylist(playlist: Playlist) = db.getPlaylistDao().deletePlayList(playlist)

    fun getAllPlaylists() = db.getPlaylistDao().getAllPlaylist()
    fun searchPlaylist(query: String) = db.getPlaylistDao().searchPlaylist(query)



    // Songs
    suspend fun upsertSong(song: Song) = db.getSongDao().upsertSong(song)
    suspend fun deleteSong(song: Song) = db.getSongDao().deleteSong(song)

    fun getAllSongs() = db.getSongDao().getAllSongs()
    fun searchSong(query: String) = db.getSongDao().searchSong(query)
}