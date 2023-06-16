package com.example.groove.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.groove.model.Playlist
import com.example.groove.model.Song

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlayList(playlist: Playlist)

    @Query("SELECT * FROM PLAYLISTS")
    fun getAllPlaylist() : List<Playlist>

    @Query("SELECT * FROM PLAYLISTS WHERE title LIKE :query")
    fun searchPlaylist(query: String?): List<Playlist>
}