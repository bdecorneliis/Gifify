package com.underdesign.gifify.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.underdesign.gifify.Singleton
import com.underdesign.gifify.liveData.FavoriteLiveData
import com.underdesign.gifify.liveData.GifLiveData

class GifModel(application: Application) : AndroidViewModel(application) {

    private var gifsList: GifLiveData? = null
    private var favoriteGifs: FavoriteLiveData? = null
    private var singleton:Singleton? = null

    fun getGifsList(): GifLiveData? {
        return gifsList
    }

    fun getFavoriteGifsList(): FavoriteLiveData? {
        singleton!!.updateFavoriteGifsList()
        return favoriteGifs
    }

    fun addPage(){
        singleton!!.addPage(singleton!!.limit * ++singleton!!.offset)
    }

    init {
        singleton = Singleton.getInstance(application,null)
        if (gifsList == null) gifsList = singleton!!.GifLiveData
        if (favoriteGifs == null) favoriteGifs = singleton!!.FavoriteLiveData
    }
}