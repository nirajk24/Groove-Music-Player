package com.example.groove.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.groove.util.Constant.Companion.PREFERENCE_NAME
import com.example.groove.util.Constant.Companion.SORTING_ORDER
import com.example.groove.util.Constant.Companion.SORT_BY
import com.example.groove.util.Constant.Companion.TITLE
import com.google.gson.Gson

class PrefRepository(val context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()
    private val gson = Gson()

    private fun String.put(long: Long) {
        editor.putLong(this, long)
        editor.commit()
    }

    private fun String.put(int: Int) {
        editor.putInt(this, int)
        editor.commit()
    }

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.put(boolean: Boolean) {
        editor.putBoolean(this, boolean)
        editor.commit()
    }

    private fun String.put(songTitleSet : Set<String>){
        editor.putStringSet(this, songTitleSet)
        editor.apply()
    }

    private fun String.getLong() = pref.getLong(this, 0)

    private fun String.getInt() = pref.getInt(this, 0)

    private fun String.getString() = pref.getString(this, "")!!

    private fun String.getBoolean() = pref.getBoolean(this, false)



    // Sorting
    fun setSortingOrder(bool : Boolean){
        SORTING_ORDER.put(bool)
    }
    fun getSortingOrder() : Boolean{
        return SORTING_ORDER.getBoolean() ?: true
    }

    fun setSortBy(s : String){
        SORT_BY.put(s)
    }
    fun getSortBy() : String{
        return SORT_BY.getString() ?: TITLE
    }



}