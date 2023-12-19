package kz.kbtu.olx.data.datasources.retrofit

import kz.kbtu.olx.Utils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    val api: ApiInterface by lazy {

        Retrofit.Builder()
            .baseUrl(Utils.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}