package com.underdesign.gifify.Provider

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import androidx.room.Room
import com.underdesign.gifify.DataBase.GifDatabase
import com.underdesign.gifify.LiveData.FavoriteLiveData
import com.underdesign.gifify.LiveData.GifLiveData
import com.underdesign.gifify.Model.Gif

class Singleton private constructor(context: Context, view: View?) {

    var GifLiveData: GifLiveData? = null
    var FavoriteLiveData: FavoriteLiveData? = null
    private val mGifs: MutableList<Gif> = ArrayList()
    var database: GifDatabase

    var offset = 0
    var limit = 30

    init{
        Companion.context = context
        Companion.view = view
        database =  Room.databaseBuilder(context, GifDatabase::class.java, "gifify-db").build()
        GifLiveData = GifLiveData(context,mGifs,0)
        FavoriteLiveData = FavoriteLiveData(context,database)
    }

    fun addPage(offset:Int){
        GifLiveData = GifLiveData(context!!,mGifs,offset)
    }

    fun updateFavoriteGifsList(){
        FavoriteLiveData!!.getFavoriteGifs()
    }

    fun checkNetwork() =
        (context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var singleton: Singleton? = null
        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null
        @SuppressLint("StaticFieldLeak")
        private var view: View? = null


        @Synchronized
        fun getInstance(context: Context, view: View?): Singleton {

            if (singleton == null) {
                singleton =
                    Singleton(context, view)
            }
            return singleton!!
        }
    }
}