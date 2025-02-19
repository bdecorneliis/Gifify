package com.underdesign.gifify

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.AbsListView
import android.widget.GridView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.underdesign.gifify.Model.GifAdapter
import com.underdesign.gifify.Model.GifModel
import com.underdesign.gifify.Provider.Singleton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var gifModel: GifModel? = null
    private var gridView: GridView? = null
    private var adapter: GifAdapter? = null
    private var singleton: Singleton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.ic_launcher)
        supportActionBar!!.setDisplayUseLogoEnabled(true)

        singleton = Singleton.getInstance(application,null)

        gridView = findViewById<View>(R.id.grid) as GridView
        adapter = GifAdapter(this,true)
        gridView!!.adapter = adapter

        gifModel = ViewModelProvider(this).get(GifModel::class.java)

        gridView!!.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (totalItemCount - visibleItemCount -15 <= firstVisibleItem && firstVisibleItem !=0 && totalItemCount != singleton!!.limit * singleton!!.offset) {
                    gifModel!!.addPage()
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, state: Int) {
            }
        })

        if(singleton!!.checkNetwork()) {
            gifModel!!.getGifsList()!!.observe(this,
                Observer{ gifsList ->
                    adapter!!.setData(gifsList)
                    adapter!!.notifyDataSetChanged()

                    if(gifsList!!.isEmpty()){
                        emptyGifContainerMain.visibility = View.VISIBLE
                    }else{
                        emptyGifContainerMain.visibility = View.GONE
                    }

                })
        }else{
            connectionContainer.visibility = View.VISIBLE
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        return if (id == R.id.favorite_icon) {
            startActivity(Intent(this,FavoriteActivity::class.java))
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem = menu!!.findItem(R.id.search_icon)
        val searchView  = menuItem.actionView as SearchView
        searchView.maxWidth  = Integer.MAX_VALUE
        searchView.queryHint = getString(R.string.searchHint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                gifModel!!.searchText(query)
                return false
            }

            override fun onQueryTextChange(value: String): Boolean {

                return true
            }

        })

        searchView.setOnCloseListener {
            gifModel!!.reset()
            false
        }
        return true
    }


}