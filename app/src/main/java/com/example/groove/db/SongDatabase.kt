package com.example.groove.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.groove.model.Song

@Database(entities = [Song::class], version = 1, exportSchema = false)
abstract class SongDatabase : RoomDatabase() {

    abstract fun getSongDao(): SongDao

    companion object {
        @Volatile  // -> Any change is visible to any other thread
        var INSTANCE: SongDatabase? = null

        @Synchronized  // -> ensures that only 1 database instance is created
        fun getInstance(context: Context): SongDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    SongDatabase::class.java,
                    "song.db"
                )
                    .fallbackToDestructiveMigration()  // -> In case database version is changed new database is created with same data
                    .build()
            }
            return INSTANCE as SongDatabase
        }
    }
}