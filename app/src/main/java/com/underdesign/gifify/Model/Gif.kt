package com.underdesign.gifify.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gif_entity")
data class Gif (
    @PrimaryKey(autoGenerate = true)
    var id:Int?,

    @ColumnInfo(name = "id_gif")
    var id_gif: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "url")
    var url: String
   )