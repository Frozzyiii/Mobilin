package com.abel.mobilin.DatabaseAPI

import com.google.firebase.firestore.PropertyName

data class Mobil(
    var id: String = "",

    @get:PropertyName("Nama")
    @set:PropertyName("Nama")
    var nama: String? = null,

    @get:PropertyName("Merk")
    @set:PropertyName("Merk")
    var merk: String? = null,

    @get:PropertyName("Transmisi")
    @set:PropertyName("Transmisi")
    var transmisi: String? = null,

    @get:PropertyName("Seat")
    @set:PropertyName("Seat")
    var seat: String? = null,

    @get:PropertyName("Harga")
    @set:PropertyName("Harga")
    var harga: Int? = 0,

    @get:PropertyName("Foto")
    @set:PropertyName("Foto")
    var foto: String? = null
)