package com.underdesign.gifify.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gif_entity")
class Gif {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
    var id_gif: String? = null
    var title: String? = null
    var URL: String? = null
}