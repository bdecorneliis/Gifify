package com.underdesign.gifify.liveData

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.underdesign.gifify.model.Gif

class FavoriteLiveData (var context: Context) : MutableLiveData<List<Gif>?>() {

    init {
        favoriteGifs()
    }

    fun favoriteGifs(){
        val gifsFavorites:MutableList<Gif> = ArrayList()
        val files = context.getExternalFilesDir(null)!!.listFiles()
        if (files != null) {
            for (file in files) {
                if (file != null && file.name != "sharingGif.gif") {

                    val gif = Gif()
                    gif.id = ""
                    gif.title = ""
                    gif.URL = file.absoluteFile.toString()

                    gifsFavorites.add(gif)

                    postValue(gifsFavorites)

                }
            }
        }
        if(gifsFavorites.isEmpty()){
            postValue(gifsFavorites)
        }
    }
}