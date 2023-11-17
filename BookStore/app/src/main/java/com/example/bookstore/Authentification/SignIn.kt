package com.example.bookstore.Authentification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.asLiveData
import com.example.bookstore.AdminMode.AdminMode
import com.example.bookstore.MainActivity
import com.example.bookstore.UserDatabase.UserDb
import com.example.bookstore.databinding.ActivitySignInBinding
import java.util.regex.Pattern

class SignIn : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailPattern = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+").toRegex()
        binding.btnadmin.setOnClickListener {

            val email = binding.inemail.text.toString()
            val password = binding.inpassword.text.toString()

            if(email.isEmpty()) binding.inemail.error = "Empty email"

            else if(!email.matches(emailPattern)) binding.inemail.error = "Incorrect email"

            else if(password.isEmpty()) binding.inpassword.error = "Empty password"

            else if(email == "admin@kbtu.kz" && password == "admin123"){
                Toast.makeText(applicationContext, "Sir, Welcome to Admin mode!", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, AdminMode::class.java)
                startActivity(intent)
                binding.inemail.text.clear()
                binding.inpassword.text.clear()
            }
            else{
                Toast.makeText(applicationContext, "Not correct!", Toast.LENGTH_SHORT).show()

            }
        }


        val db = UserDb.getUserDb(this)
        binding.enterbtn.setOnClickListener {

            val email = binding.inemail.text.toString()
            val password = binding.inpassword.text.toString()

            if(email.isEmpty()) binding.inemail.error = "Empty email"

            else if(!email.matches(emailPattern)) binding.inemail.error = "Incorrect email"

            else if(password.isEmpty()) binding.inpassword.error = "Empty password"

            else{
                db.getUserDao().getAllUser().asLiveData().observe(this){list->
                    var target:Boolean = true
                    list.forEach{
                        if((email == it.email) && (password == it.password)){
                            Toast.makeText(applicationContext, "Successful Authorization", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            binding.inemail.text.clear()
                            binding.inpassword.text.clear()
                            target = true
                            return@forEach
                        }
                        else{
                            target = false
                        }
                    }
                    if(!target){
                        Toast.makeText(applicationContext, "Not correct!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        binding.replace.setOnClickListener {
            val intent = Intent(applicationContext, Signup::class.java)
            startActivity(intent)
        }
    }
}