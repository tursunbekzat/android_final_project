package kz.kbtu.olx.presentation.activities.ui.my_ads

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.presentation.adapters.AdAdapter
import kz.kbtu.olx.databinding.ActivityAdSellerBinding
import kz.kbtu.olx.domain.models.Ad

class SellerInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdSellerBinding


    private var sellerUid = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAdSellerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sellerUid = intent.getStringExtra("sellerUid").toString()

        loadSellerInfo()

        loadAds()

        binding.toolbarBackBtn.setOnClickListener {

            onBackPressed()
        }
    }


    private fun loadSellerInfo(){

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(sellerUid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val name = "${snapshot.child("name").value}"
                    val profileImageUrl = "${snapshot.child("profileImageUrl").value}"
                    val timestamp = snapshot.child("timestamp").value as Long

                    val formattedData = Utils.formatTimestampDate(timestamp)

                    binding.sellerNameTv.text = name
                    binding.memberSinceTv.text = formattedData

                    try {

                        Glide.with(this@SellerInfoActivity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_person_white)
                            .into(binding.sellerProfileIv)
                    } catch (e: Exception) {

                        Log.e(TAG, "onDataChange: ", e)
                    }


                }
                override fun onCancelled(error: DatabaseError) {


                }
            })
    }


    private fun loadAds(){

        val adArrayList: ArrayList<Ad> = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Ads")
        ref.orderByChild("uid").equalTo(sellerUid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    adArrayList.clear()

                    for (ds in snapshot.children){

                        try {

                            val ad = ds.getValue(Ad::class.java)
                            adArrayList.add(ad!!)
                        } catch (e: Exception){

                            Log.e(TAG, "onDataChange: ", e)
                        }
                    }

                    val adapterAd = AdAdapter(this@SellerInfoActivity, adArrayList)
                    binding.adsRv.adapter = adapterAd

                    val adsCount = adArrayList.size
                    binding.publishedAdsCountTv.text = "$adsCount"
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


    private companion object{

        private const val TAG = "AD_SELLER_PROFILE_TAG"
    }
}