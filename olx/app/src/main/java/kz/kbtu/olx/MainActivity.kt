package kz.kbtu.olx

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kz.kbtu.olx.auth.LoginOptionsActivity
import kz.kbtu.olx.databinding.ActivityMainBinding
import kz.kbtu.olx.fragments.AccountFragment
import kz.kbtu.olx.fragments.ChatsFragment
import kz.kbtu.olx.fragments.HomeFragment
import kz.kbtu.olx.fragments.MyAdsFragment
import kz.kbtu.olx.ui.sell.CreateAdActivity

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
        } else {

            updateFcmToken()
            askNotificationPermission()
        }

        showHomeFragment()

        navigationSelected()

        binding.sellFav.setOnClickListener {

            if (firebaseAuth.currentUser == null) {

                Utils.toast(this, "Login Required")
                startLoginOptions()
            }
            else {


                val intent = Intent(this, CreateAdActivity::class.java)
                intent.putExtra("isEditMode", false)
                startActivity(intent)
            }
        }

    }


    private fun navigationSelected(){

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


    private fun updateFcmToken(){

        val myUid = "${firebaseAuth.uid}"

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { fcmToken ->

                val hashMap = HashMap<String, Any>()
                hashMap["fcmToken"] = "$fcmToken"

                val ref = FirebaseDatabase.getInstance().getReference("Users")
                ref.child(myUid)
                    .updateChildren(hashMap)
                    .addOnSuccessListener {

                        Log.d(TAG, "updateFcmToken: FCM Token updated to db Success")
                    }
                    .addOnFailureListener { e ->

                        Log.e(TAG, "updateFcmToken: ", e)
                    }
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "updateFcmToken: ", e)
            }
    }


    private fun askNotificationPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED){

                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->

        Log.d(TAG, "requestNotificationPermission: isGranted: $isGranted")
    }


    private companion object {

        private const val TAG = "MAIN_ACTIVITY_TAG"
    }
}