package com.underdesign.gifify.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.underdesign.gifify.Model.Gif

@Database(entities = [Gif::class], version = 1)
abstract class GifDatabase : RoomDatabase() {
    abstract fun gifDao(): GifDao
}