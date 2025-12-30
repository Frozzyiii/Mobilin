package com.abel.mobilin.DatabaseAPI

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("get_mobil.php")
    fun getMobil(): Call<List<Mobil>>
}