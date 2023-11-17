package com.example.bookstore.Authentification


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bookstore.UserDatabase.User
import com.example.bookstore.UserDatabase.UserDb
import com.example.bookstore.databinding.ActivitySignupBinding
import java.util.regex.Pattern

class Signup : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.upsignupbtn.setOnClickListener{
            val username = binding.username.text.toString()
            val email = binding.upemail.text.toString()
            val password = binding.uppassword.text.toString()
            val repassword = binding.uprepassword.text.toString()

            val db = UserDb.getUserDb(this)

            val emailPattern = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+").toRegex()

            if(username.isEmpty()) binding.username.error = "Empty username"
            else if(!email.matches(emailPattern)) binding.upemail.error = "Incorrect email"
            else if(password.length < 8) binding.uppassword.error = "Password length should be more than 7"
            else if(repassword != password) binding.uprepassword.error = "Not matches to password"
            else{
                binding.upsignupbtn.setOnClickListener {
                    val client = User(null, username, email, password)
                    Thread{
                        db.getUserDao().insertUser(client)
                        binding.username.text.clear()
                        binding.upemail.text.clear()
                        binding.uppassword.text.clear()
                        binding.uprepassword.text.clear()
                    }.start()
                    Toast.makeText(applicationContext, "Registration was Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, SignIn::class.java)
                    startActivity(intent)
                }
            }


        }
    }
}