package com.example.bookstore.Retrofit

import retrofit2.http.GET

interface Apinterface {

    @GET("/Elixirs")
    suspend fun getData() : List<Elixirs>
}
