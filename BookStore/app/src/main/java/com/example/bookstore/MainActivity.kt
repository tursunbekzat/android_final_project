package com.example.bookstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.bookstore.Authentification.Welcome
import com.example.bookstore.Fragments.CreateBook
import com.example.bookstore.Fragments.Mainpage
import com.example.bookstore.Fragments.SavedActivity
import com.example.bookstore.Retrofit.RetrofitInstance
import com.example.bookstore.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            navigation.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.item1 -> {
                        Toast.makeText(applicationContext, "item1", Toast.LENGTH_SHORT).show()
                    }
                    R.id.item2 ->{
                        Toast.makeText(applicationContext, "item2", Toast.LENGTH_SHORT).show()
                    }
                    R.id.item3 ->{
                        supportFragmentManager
                            .beginTransaction().
                            replace(R.id.place_holder, CreateBook.newInstance())
                            .commit()
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    R.id.item4 ->{
                        Toast.makeText(applicationContext, "item4", Toast.LENGTH_SHORT).show()

                    }
                    R.id.item5 ->{
                        val intent = Intent(this@MainActivity, SavedActivity::class.java)
                        startActivity(intent)
                    }

                }
                true
            }
        }

        NavHostFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.place_holder, Mainpage.newInstance())
            .commit()

        binding.bottomMenu.selectedItemId = R.id.mainpage
        binding.apply {
            bottomMenu.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu -> {
                        drawer.openDrawer(GravityCompat.START)
                    }
                    R.id.data -> {

                    }
                    R.id.mainpage -> {
                        supportFragmentManager
                            .beginTransaction().
                            replace(R.id.place_holder, Mainpage.newInstance())
                            .commit()
                    }
                    R.id.logout -> {
                        Toast.makeText(applicationContext, "You`ve been Logged out!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, Welcome::class.java)
                        startActivity(intent)
                    }
                }
                true
            }
        }
    }
}