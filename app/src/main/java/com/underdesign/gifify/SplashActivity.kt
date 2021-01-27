package com.underdesign.gifify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.underdesign.gifify.Model.GifModel
import com.underdesign.gifify.Provider.Singleton


class SplashActivity : AppCompatActivity() {
    private var singleton: Singleton? = null
    private var gifModel: GifModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        singleton = Singleton.getInstance(this,null)

        val logo = findViewById<ImageView>(R.id.imageLogo)
        val stb = AnimationUtils.loadAnimation(this,R.anim.stb)

        logo.startAnimation(stb)

        //Se chequea que el dispositivo este conectado a internet
        if(singleton!!.checkNetwork()){
            //Dejo el observer para que SplashActivity sea notificado cuando LiveData
            //termina de descargary pueda abrir MainActivity

            gifModel = ViewModelProvider(this).get(GifModel::class.java)
            gifModel!!.getGifsList()!!.observe(this,
                Observer{
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                })
        }else{
            startActivity(Intent(this@SplashActivity, FavoriteActivity::class.java))
            finish()
        }

    }

}