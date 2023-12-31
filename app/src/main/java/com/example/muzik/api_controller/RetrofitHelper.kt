package com.example.muzik.api_controller

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    //const val baseUrl = "http://10.0.2.2:6600/"
    //const val baseUrl = "http://192.168.1.24:6600/"
    const val baseUrl = "https://muzik-server-antooo5113.onrender.com"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}