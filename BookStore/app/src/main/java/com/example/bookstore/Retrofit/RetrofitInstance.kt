package com.example.bookstore.Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://wizard-world-api.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    val apinterface by lazy{

        retrofit.create(Apinterface::class.java)
    }
}