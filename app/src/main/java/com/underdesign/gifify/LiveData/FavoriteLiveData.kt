package com.underdesign.gifify.LiveData

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.RoomDatabase
import com.underdesign.gifify.DataBase.GifDatabase
import com.underdesign.gifify.Model.Gif
import com.underdesign.gifify.Provider.Singleton
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class FavoriteLiveData (var context: Context,var database: GifDatabase) : MutableLiveData<List<Gif>?>() {

    init {
        getFavoriteGifs()
    }

    fun getFavoriteGifs() {
        doAsync {
            val gifs = database.gifDao().getAllGifs()
            uiThread {
                postValue(gifs)
            }
        }
    }
}