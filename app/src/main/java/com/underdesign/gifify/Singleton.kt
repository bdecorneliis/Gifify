package com.underdesign.gifify

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import com.underdesign.gifify.liveData.FavoriteLiveData
import com.underdesign.gifify.liveData.GifLiveData
import com.underdesign.gifify.model.Gif

class Singleton private constructor(context: Context, view: View?) {

    var GifLiveData: GifLiveData? = null
    var FavoriteLiveData: FavoriteLiveData? = null
    private val mGifs: MutableList<Gif> = ArrayList()

    var offset = 0
    var limit = 30

    init{
        Singleton.context = context
        Singleton.view = view
        GifLiveData = GifLiveData(context,mGifs,0)
        FavoriteLiveData = FavoriteLiveData(context)
    }

    fun addPage(offset:Int){
        GifLiveData = GifLiveData(context!!,mGifs,offset)
    }

    fun updateFavoriteGifsList(){
        FavoriteLiveData!!.favoriteGifs()
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
        fun getInstance(context: Context, view: View?): Singleton{

            if (singleton == null) {
                singleton = Singleton(context,view)
            }
            return singleton!!
        }
    }
}