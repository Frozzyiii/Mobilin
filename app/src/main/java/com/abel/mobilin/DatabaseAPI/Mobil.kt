package com.abel.mobilin.DatabaseAPI

import com.google.firebase.firestore.PropertyName

data class Mobil(
    var id: String = "",

    @get:PropertyName("Nama")
    var nama: String? = null,

    @get:PropertyName("Merk")
    var merk: String? = null,

    @get:PropertyName("Transmisi")
    var transmisi: String? = null,

    @get:PropertyName("Seat")
    var seat: String? = null,

    @get:PropertyName("Harga")
    var harga: Int? = 0,

    @get:PropertyName("Foto")
    var foto: String? = null
)