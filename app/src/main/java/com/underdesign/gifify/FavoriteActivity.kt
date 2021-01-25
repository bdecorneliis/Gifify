package com.underdesign.gifify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.GridView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.underdesign.gifify.model.GifAdapter
import com.underdesign.gifify.model.GifModel
import kotlinx.android.synthetic.main.activity_favorite.*

class FavoriteActivity : AppCompatActivity() {

    private var gifModel: GifModel? = null
    private var gridView: GridView? = null
    private var adapter: GifAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        gridView = findViewById<View>(R.id.grid) as GridView
        adapter = GifAdapter(this,false)
        gridView!!.adapter = adapter


        gifModel = ViewModelProvider(this).get(GifModel::class.java)

        gifModel!!.getFavoriteGifsList()!!.observe(this,
            Observer{ gifsList ->
                adapter!!.setData(gifsList)
                adapter!!.notifyDataSetChanged()

                if(gifsList!!.isEmpty()){
                    emptyGifContainerFavorite.visibility = View.VISIBLE
                }else{
                    emptyGifContainerFavorite.visibility = View.GONE
                }

            })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        startActivity(Intent(this@FavoriteActivity,MainActivity::class.java))
        finish()
    }

}