package com.underdesign.gifify.Model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.underdesign.gifify.Provider.Singleton
import com.underdesign.gifify.LiveData.FavoriteLiveData
import com.underdesign.gifify.LiveData.GifLiveData

class GifModel(application: Application) : AndroidViewModel(application) {

    private var gifsList: GifLiveData? = null
    private var favoriteGifs: FavoriteLiveData? = null
    private var singleton: Singleton? = null

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


    fun searchText(value:String){
        gifsList!!.searchText(value)
    }

    fun reset(){
        gifsList!!.reset()
    }


    init {
        singleton = Singleton.getInstance(application,null)
        if (gifsList == null) gifsList = singleton!!.GifLiveData
        if (favoriteGifs == null) favoriteGifs = singleton!!.FavoriteLiveData
    }
}