package com.example.bookstore.Authentification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.bookstore.Retrofit.RetrofitInstance

import com.example.bookstore.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Welcome : AppCompatActivity() {
    lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signupbtn.setOnClickListener{
            val intent = Intent(applicationContext, SignIn::class.java)
            startActivity(intent)
        }
    }
}