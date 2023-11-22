package kz.kbtu.olx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kz.kbtu.olx.ui.LoginOptionsActivity
import kz.kbtu.olx.databinding.ActivityMainBinding
import kz.kbtu.olx.fragments.AccountFragment
import kz.kbtu.olx.fragments.ChatsFragment
import kz.kbtu.olx.fragments.HomeFragment
import kz.kbtu.olx.fragments.MyAdsFragment
import kz.kbtu.olx.ui.CreateAdActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null){

            Log.d("Test", "firebase is null")
            startLoginOptions()
        }
        else{

            Log.d("Test", "firebase is not null ${firebaseAuth.currentUser}")
        }

        showHomeFragment()

        binding.bottomNav.setOnItemSelectedListener { item->

            when(item.itemId){
                R.id.menu_home -> {

                    showHomeFragment()
                    true
                }
                R.id.menu_chats -> {
                    if (firebaseAuth.currentUser == null) {

                        Utils.toast(this, "Login Required")
                        startLoginOptions()

                        false
                    }
                    else {

                        showChatsFragment()
                         true
                    }
                }
                R.id.menu_my_ads -> {
                    if (firebaseAuth.currentUser == null) {

                        Utils.toast(this, "Login Required")
                        startLoginOptions()

                        false
                    }
                    else {
                        showMyAdsFragment()
                        true
                    }
                }
                R.id.menu_account -> {
                    if (firebaseAuth.currentUser == null) {

                        Utils.toast(this, "Login Required")
                        startLoginOptions()

                        false
                    }
                    else {

                        showAccountFragment()
                        true
                    }

                }
                else -> {

                    false
                }
            }
        }

        binding.sellFav.setOnClickListener {
            startActivity(Intent(this, CreateAdActivity::class.java))
        }
    }
    private fun showHomeFragment(){

        binding.toolbarTitleTv.text = "Home"

        val fragment = HomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFL.id, fragment, "HomeFragment")
        fragmentTransaction.commit()
    }

    private fun showChatsFragment(){

        binding.toolbarTitleTv.text = "Chats"

        val fragment = ChatsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFL.id, fragment, "ChatsFragment")
        fragmentTransaction.commit()
    }

    private fun showMyAdsFragment(){

        binding.toolbarTitleTv.text = "My Ads"

        val fragment = MyAdsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFL.id, fragment, "MyAdsFragment")
        fragmentTransaction.commit()
    }

    private fun showAccountFragment(){

        binding.toolbarTitleTv.text = "Account"

        val fragment = AccountFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFL.id, fragment, "AccountFragment")
        fragmentTransaction.commit()
    }

    private fun startLoginOptions(){

        startActivity(Intent(this, LoginOptionsActivity::class.java))
    }
}