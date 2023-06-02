package com.example.groove.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "songs")
@Parcelize
data class Song(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "album")
    val album: String,
    @ColumnInfo(name = "artist")
    val artist: String,
    @ColumnInfo(name = "duration")
    val duration: Long = 0,
    @ColumnInfo(name = "path")
    val path: String,
    @ColumnInfo(name = "artUri")
    val artUri: String
) : Parcelable
