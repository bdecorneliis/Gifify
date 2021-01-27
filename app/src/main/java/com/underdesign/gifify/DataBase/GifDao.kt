package com.underdesign.gifify.DataBase

import androidx.room.*
import com.underdesign.gifify.Model.Gif

@Dao
interface GifDao {

    @Query("SELECT * FROM gif_entity")
    fun getAllGifs(): MutableList<Gif>

    @Query("SELECT COUNT(*) FROM gif_entity where id_gif like :id_gif")
    fun getGifById(id_gif: String): Int

    @Insert
    fun addGif(gifEntity : Gif):Long

    @Delete
    fun deleteGif(gifEntity: Gif):Int


}