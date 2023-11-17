package com.example.bookstore.AdminMode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookstore.Authentification.SignIn
import com.example.bookstore.BookDatabase.ElixirsDB
import com.example.bookstore.databinding.ActivityAdminModeBinding

class AdminMode : AppCompatActivity() {

    lateinit var binding: ActivityAdminModeBinding
    private val adapter = AdminBookAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var builder = AlertDialog.Builder(this)


        val db = ElixirsDB.getBookDb(this)

        db.getBookDao().getAllBook().asLiveData().observe(this){list->
            list.forEach{
//                adapter.addAdminBook(it)
            }
            binding.rcview.layoutManager = LinearLayoutManager(this)
            binding.rcview.adapter = adapter

        }

        binding.btnlogout.setOnClickListener {
            builder.setTitle("Log out")
                .setMessage("Log out from Admin mode?")
                .setPositiveButton("Yep"){inter, it->
                    val intent = Intent(applicationContext, SignIn::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Nope"){inter, it->
                    inter.cancel()
                }
                .show()
        }

    }
}