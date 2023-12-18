package kz.kbtu.olx

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.kbtu.olx.adapter.AdapterAd
import kz.kbtu.olx.adapter.AdapterChats
import kz.kbtu.olx.adapter.AdapterImagePicked
import kz.kbtu.olx.models.ModelAd
import kz.kbtu.olx.models.ModelChats
import kz.kbtu.olx.models.ModelImagePicked
import java.util.Arrays
import java.util.Calendar
import java.util.Locale

object Utils {

    const val ADD_STATUS_AVAILABLE = "AVAILABLE"
    const val ADD_STATUS_SOLD = "SOLD"
    const val MESSAGE_TYPE_TEXT = "TEXT"
    const val MESSAGE_TYPE_IMAGE = "IMAGE"
    const val NOTIFICATION_TYPE_NEW_MESSAGE = "NOTIFICATION_TYPE_NEW_MESSAGE"
    const val FCM_SERVER_KEY = "AAAA8XngBkY:APA91bEWmN422ffj0c5n9wqI_2KmAN7L3watA5E6M2Rq3DYjPOQbKd23NSrcLaJlevVKye07qplVBa_JaFV4i-P7K3Wf0MMCYARmmR-jhK438tgtBL4eun5gLmxDoctZJog-5uwVldeO"


    val categories = arrayOf(

        "All",
        "Mobiles",
        "Computer/Laptop",
        "Electronics & Home Appliances",
        "Vehicles",
        "Furniture & Home Decor",
        "Fashion & Beauty",
        "Books",
        "Sports",
        "Animals",
        "Businesses",
        "Agricultural"
    )
    val categoryIcons = arrayOf(

        R.drawable.ic_category_all,
        R.drawable.ic_category_mobiles,
        R.drawable.ic_category_computers,
        R.drawable.ic_category_home,
        R.drawable.ic_category_vehicle,
        R.drawable.ic_category_furniture,
        R.drawable.ic_category_fashion,
        R.drawable.ic_category_books,
        R.drawable.ic_category_sports,
        R.drawable.ic_category_animals,
        R.drawable.ic_category_business,
        R.drawable.ic_category_agricultural
    )
    val conditions = arrayOf(

        "New",
        "Used",
        "Refurbished"
    )


    fun toast(context: Context, message: String){

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    fun getTimestamp() : Long{

        return System.currentTimeMillis()
    }


    fun formatTimestampDate(timestamp: Long): String{

        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp

        return DateFormat.format("dd/MM/yyyy", calendar).toString()
    }


    fun formatTimestampDateTime(timestamp: Long): String{

        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp

        return DateFormat.format("dd/MM/yyyy hh:mm:a", calendar).toString()
    }


    fun addToFavorite(context: Context, adId: String) {

        val firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null) {

            toast(context, "You are not logged-in!")
        } else {

            val timestamp = getTimestamp()

            val hashMap = HashMap<String, Any>()
            hashMap["adId"] = adId
            hashMap["timestamp"] = timestamp

            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseAuth.uid!!).child("Favorites").child(adId)
                .setValue(hashMap)
                .addOnSuccessListener {

                    toast(context, "Added to favorite!")
                }
                .addOnFailureListener{ e->

                    toast(context, "Failed to add to favorite due to ${e.message}")
                }
        }
    }


    fun removeFromFavorite(context: Context, adId: String) {

        val firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null) {

            toast(context, "You are not logged-in!")
        } else {

            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseAuth.uid!!).child("Favorites").child(adId)
                .removeValue()
                .addOnSuccessListener {

                    toast(context, "Removed from favorite!")
                }
                .addOnFailureListener{ e->

                    toast(context, "Failed to remove from favorites due to ${e.message}")
                }
        }
    }


    fun callIntent(context: Context, phone: String) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tel:${Uri.encode(phone)}"))
        context.startActivity(intent)
    }


    fun smsIntent(context: Context, phone: String) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${Uri.encode(phone)}"))
        context.startActivity(intent)
    }


    fun mapIntent(context: Context, latitude: Double, longuitude: Double){

        val gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=$latitude,$longuitude")

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.com.android.apps.maps")

        if (mapIntent.resolveActivity(context.packageManager) != null){

            context.startActivity(mapIntent)
        } else {

            toast(context, "Google Map not installed")
        }
    }


    fun chatPath(receiptUid: String, yourUid: String) : String {

        val arrayUids = arrayOf(receiptUid, yourUid)
        Arrays.sort(arrayUids)

        return "${arrayUids[0]}_${arrayUids[1]}"
    }


    fun checkIsFavorite(modelAd: ModelAd, holder: AdapterAd.AdViewHolder, context: Context, firebaseAuth: FirebaseAuth) {

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(modelAd.id)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val favorite = snapshot.exists()
                    modelAd.favorite = favorite

                    if (favorite) {

                        holder.favBtn.setImageResource(R.drawable.ic_fav)
                    } else {

                        holder.favBtn.setImageResource(R.drawable.ic_fav_no)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                    toast(context, "Failed ")
                }
            })
    }


    fun loadFirstImage(modelAd: ModelAd, holder: AdapterAd.AdViewHolder, context: Context) {

        val aId = modelAd.id

        val reference = FirebaseDatabase.getInstance().getReference("Ads")
        reference.child(aId).child("Images").limitToFirst(1)
            .addValueEventListener(object: ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (ds in snapshot.children) {

                        val imageUrl = "${ds.child("imageUrl").value}"

                        try {

                            Glide.with(context)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_image_gray)
                                .into(holder.imageIv)

                        } catch (e: Exception) {

                            Log.e("AD_ADAPTER_TAG", "loadFirstImage: onDataChange: ", e)
                        }
                    }
                }

                override fun onCancelled(e: DatabaseError) {

                }
            })
    }
}