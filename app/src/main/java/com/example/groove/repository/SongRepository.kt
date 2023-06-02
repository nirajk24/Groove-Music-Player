package com.example.groove.repository

import com.example.groove.db.SongDatabase
import com.example.groove.model.Song

class SongRepository(private val db: SongDatabase) {

    suspend fun upsertSong(song: Song) = db.getSongDao().upsertSong(song)
    suspend fun deleteSong(song: Song) = db.getSongDao().deleteSong(song)

    fun getAllSongs() = db.getSongDao().getAllSongs()
    fun searchSong(query: String) = db.getSongDao().searchSong(query)
}