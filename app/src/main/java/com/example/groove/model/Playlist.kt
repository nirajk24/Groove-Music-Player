package com.example.groove.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "playlist")
@Parcelize
data class Playlist(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "numOfSongs")
    val numOfSongs: Int,

    @ColumnInfo(name = "songList")
    val songList: List<Song>

) : Parcelable
