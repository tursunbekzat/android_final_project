package com.example.bookstore.Retrofit

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bookstore.R

class MainActivity2 : AppCompatActivity( ) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getData()
    }

    private fun getData() {

//        RetrofitInstance.apinterface.getData()
    }
}