package com.example.groove.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.groove.model.Playlist
import com.example.groove.model.Song

@Database(entities = [Song::class, Playlist::class], version = 1, exportSchema = false)
abstract class PlaylistDatabase : RoomDatabase() {

    abstract fun getSongDao(): SongDao
    abstract fun getPlaylistDao(): PlaylistDao

    companion object {
        @Volatile  // -> Any change is visible to any other thread
        var INSTANCE: PlaylistDatabase? = null

        @Synchronized  // -> ensures that only 1 database instance is created
        fun getInstance(context: Context): PlaylistDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    PlaylistDatabase::class.java,
                    "db"
                )
                    .fallbackToDestructiveMigration()  // -> In case database version is changed new database is created with same data
                    .build()
            }
            return INSTANCE as PlaylistDatabase
        }
    }
}