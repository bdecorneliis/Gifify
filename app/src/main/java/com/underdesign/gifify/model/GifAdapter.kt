package com.underdesign.gifify.model

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.Nullable
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.underdesign.gifify.BuildConfig
import com.underdesign.gifify.R
import com.underdesign.gifify.Singleton
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer


class GifAdapter (private val context: Context?, private val isHomeScreen:Boolean): BaseAdapter() {
    private var gifsList: List<Gif>? = ArrayList()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val db = Firebase.firestore

    fun setData(gifsList: List<Gif>?) {
        this.gifsList = gifsList
    }

    override fun getCount(): Int {
        return gifsList!!.size
    }

    override fun getItem(position: Int): Gif {
        return this.gifsList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View? {
        var newRow: View? = convertView
        if (newRow == null) {
            val inflater =
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            newRow = inflater.inflate(R.layout.grid_item, viewGroup, false)
        }

        val imageView = newRow!!.findViewById(R.id.imagen_gift) as ImageView
        val brokeView = newRow.findViewById(R.id.broke) as ImageView
        val loadingView = newRow.findViewById(R.id.loading) as ProgressBar
        loadingView.visibility = View.VISIBLE

        val id = getItem(position).id
        val title = getItem(position).title
        val imagePath = getItem(position).URL


        Glide.with(imageView.context)
            .load(imagePath)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable?> {

                override fun onLoadFailed(
                    @Nullable e: GlideException?, model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {

                    brokeView.visibility = View.VISIBLE
                    loadingView.visibility = View.GONE

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    loadingView.visibility = View.GONE

                    return false
                }
            })
            .into(imageView)

        newRow.setOnClickListener {
            Glide.with(context!!)
                .asGif().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                .load(imagePath)
                .into(object : CustomTarget<GifDrawable>(250, 250) {

                    override fun onResourceReady(
                        resource: GifDrawable, transition: Transition<in GifDrawable>?
                    ) {
                        val uri = saveImage(resource, "sharingGif.gif")
                        if (uri != null) {
                            shareGif(uri)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }

        if (isHomeScreen) {
            newRow.setOnLongClickListener(View.OnLongClickListener {
                val vibrator = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            50,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator.vibrate(200)
                }

                //Inicio. Envia informacion a Firebase
                db.collection("gifs").add(getItem(position))

                val parameters = Bundle().apply {
                    this.putString("id_gif", id)
                    this.putString("title", title)
                    this.putString("url", imagePath)
                }
                firebaseAnalytics = Firebase.analytics
                firebaseAnalytics.setDefaultEventParameters(parameters)
                //Fin. Envia informacion a Firebase

                Glide.with(context)
                    .asGif()
                    .load(imagePath)
                    .into(object : CustomTarget<GifDrawable>(250, 250) {

                        override fun onResourceReady(
                            resource: GifDrawable, transition: Transition<in GifDrawable>?
                        ) {
                            saveImage(resource, "${id}.gif")
                            val stb = AnimationUtils.loadAnimation(context, R.anim.stb)
                            val star = newRow.findViewById<ImageView>(R.id.gif_added_confirmation)
                            star.visibility = View.VISIBLE
                            star.startAnimation(stb)

                            stb.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(arg0: Animation?) {}
                                override fun onAnimationRepeat(arg0: Animation?) {}
                                override fun onAnimationEnd(arg0: Animation?) {
                                    star.visibility = View.GONE
                                }
                            })

                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })

                return@OnLongClickListener true
            })
        } else {

            newRow.setOnLongClickListener(View.OnLongClickListener {
                val vibrator = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            50,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator.vibrate(50)
                }

                val alert = android.app.AlertDialog.Builder(context)
                alert.setTitle(context.getText(R.string.dialog_title))

                alert.setMessage(context.getText(R.string.dialog_body))
                alert.setPositiveButton(
                    context.getText(R.string.yes)
                ) { dialog, _ ->
                    val myFile = File(imagePath!!)
                    if (myFile.exists()) {
                        myFile.delete()
                    }
                    dialog.dismiss()

                    val singleton = Singleton.getInstance(context, null)
                    singleton.updateFavoriteGifsList()
                    dialog.dismiss()
                }

                alert.setNegativeButton(
                    context.getText(R.string.no)
                ) { dialog, _ -> dialog.dismiss() }

                alert.show()
                return@OnLongClickListener true

            })

        }

        return newRow

    }

    private fun saveImage(gifDrawable: GifDrawable?, fileName: String): Uri? {
        gifDrawable?.let {

            val baseDir: String =   context!!.getExternalFilesDir(null)!!.absolutePath
            val sharingGifFile = File(baseDir, fileName)
            gifDrawableToFile(gifDrawable, sharingGifFile)

            return  FileProvider.getUriForFile(
                context, BuildConfig.APPLICATION_ID + ".provider",
                sharingGifFile
            )
        }
        return null
    }

    private fun shareGif(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/gif"

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        context!!.startActivity(
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.share_gif)
            )
        )
    }

    private fun gifDrawableToFile(gifDrawable: GifDrawable, gifFile: File) {
        val byteBuffer = gifDrawable.buffer
        val output = FileOutputStream(gifFile)
        val bytes = ByteArray(byteBuffer.capacity())
        (byteBuffer.duplicate().clear() as ByteBuffer).get(bytes)
        output.write(bytes, 0, bytes.size)
        output.close()
    }

}