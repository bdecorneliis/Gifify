package com.underdesign.gifify.liveData

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.underdesign.gifify.model.Gif
import com.underdesign.gifify.provider.Constant
import org.json.JSONException

class GifLiveData(private var context: Context,private var mGifs:MutableList<Gif>,private var offset:Int): MutableLiveData<List<Gif>>() {

    init {
        loadData()
    }

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

                        gif.id = obj.getString("id")
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
}