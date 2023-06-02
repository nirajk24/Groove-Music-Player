package com.example.groove.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.groove.model.Song

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Query("SELECT * FROM SONGS")
    fun getAllSongs() : LiveData<List<Song>>

    @Query("SELECT * FROM SONGS WHERE title LIKE :query")
    fun searchSong(query: String?): LiveData<List<Song>>

}