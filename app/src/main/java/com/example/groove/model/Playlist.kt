package com.example.groove.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "playlists")
@Parcelize
data class Playlist(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "numOfSongs")
    val numOfSongs: Int,

    @ColumnInfo(name = "songTitles")
    val songTitlesJson: String,

    @ColumnInfo(name = "totalDuration")
    val totalDuration: String

) : Parcelable {
    // Deserialize the JSON string back into a list of song titles
    fun getSongTitles(): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(songTitlesJson, type)
    }

    companion object {
        // Create a Playlist object by providing the list of song titles
        fun createPlaylist(title: String, songTitles: List<String>, totalDuration: String): Playlist {
            val numOfSongs = songTitles.size
            val songTitlesJson = Gson().toJson(songTitles)
            return Playlist(title, numOfSongs, songTitlesJson, totalDuration)
        }
    }
}
