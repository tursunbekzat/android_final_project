package kz.kbtu.olx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kz.kbtu.olx.UI.LoginOptionsActivity
import kz.kbtu.olx.databinding.ActivityMainBinding
import kz.kbtu.olx.fragments.AccountFragment
import kz.kbtu.olx.fragments.ChatsFragment
import kz.kbtu.olx.fragments.HomeFragment
import kz.kbtu.olx.fragments.MyAdsFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null){
            startLoginOptions()
        }

        showHomeFragment()

        binding.bottomNav.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.menu_home -> {
                    showHomeFragment()
                    true
                }
                R.id.menu_chats -> {
                    showChatsFragment()
                    true
                }
                R.id.menu_my_ads -> {
                    showMyAdsFragment()
                    true
                }
                R.id.menu_account -> {
                    showAccountFragment()
                    true
                }
                else -> {
                    false
                }
            }
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