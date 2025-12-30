package com.abel.mobilin.DatabaseAPI

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mobil_table")
data class Mobil(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val namaMobil: String = "",
    val merkMobil: String = "",
    val hargaSewa: Int = 0,
    val gambarUrl: String = ""
)