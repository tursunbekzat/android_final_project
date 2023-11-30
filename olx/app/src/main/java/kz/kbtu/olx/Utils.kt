package kz.kbtu.olx

import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

object Utils {


    const val ADD_STATUS_AVAILABLE = "AVAILABLE"
    const val ADD_STATUS_SOLD = "SOLD"


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
}