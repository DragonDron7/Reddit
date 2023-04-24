package com.dronios777.myreddit.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.dronios777.myreddit.APP_PREFERENCES
import com.dronios777.myreddit.KEY_STRING
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Token @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    //метод возращает значение из SharedPreference
    fun getDataFromSharedPreference(): String? {
        return prefs.getString(KEY_STRING, null)
    }

    //метод для записи значения в SharedPreference
    fun saveDataToSharedPreference(text: String) {
        prefs.edit().clear().apply()//полная очистка хранилища, т.к. храним один токен
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(KEY_STRING, text).apply()//записываем
    }

    //метод для стирания значения и в SharedPreference
    fun clearToken() {
        prefs.edit().remove(KEY_STRING).apply() //удаляем значения по ключу
    }

}