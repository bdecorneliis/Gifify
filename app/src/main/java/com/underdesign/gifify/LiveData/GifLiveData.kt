package com.underdesign.gifify.LiveData

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.underdesign.gifify.Model.Gif
import com.underdesign.gifify.Provider.Constant
import org.json.JSONException

class GifLiveData(private var context: Context,private var mGifs:MutableList<Gif>,private var offset:Int): MutableLiveData<List<Gif>>() {

    init {
        loadData()
    }

    private val gifsFiltered:MutableList<Gif> = ArrayList()

    private fun loadData() {
        val url = "${Constant().link}/trending?api_key=${Constant().token}&limit=30&offset=${offset}"
        val requestQueue = Volley.newRequestQueue(context)
        val getRequest =  JsonObjectRequest(
            Request.Method.GET,url,null,
            Response.Listener { response ->
                for (i in 0 until response!!.getJSONArray("data").length()) {
                    val gif = Gif()
                    try {
                        val obj = response.getJSONArray("data").getJSONObject(i)

                        gif.id_gif = obj.getString("id")
                        gif.title = obj.getString("title")
                        gif.URL = obj.getJSONObject("images").getJSONObject("downsized").getString("url")


                        mGifs.add(gif)

                        postValue(mGifs)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            },
            Response.ErrorListener { error->
                postValue(mGifs)
            }
        )
        requestQueue.add(getRequest)
    }

    fun reset(){
        postValue(mGifs)
    }

    fun searchText(value:String?):Boolean{
        mGifs.forEach{
            if(it.title!!.contains(value!!, ignoreCase = true)){
                gifsFiltered.add(it)
            }
        }

        postValue(gifsFiltered)
        return true
    }

}