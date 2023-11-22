package kz.kbtu.olx

import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import java.util.Calendar
import java.util.Locale

object Utils {


    const val ADD_STATUS_AVAILABLE = "AVAILABLE"
    const val ADD_STATUS_SOLD = "SOLD"


    val categories = arrayOf(
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


    val conditions = arrayOf(
        "New",
        "Used",
        "Refurbished"
    )

    fun toast(context: Context, message: String){

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getTimstamp() : Long{

        return System.currentTimeMillis()
    }

    fun formatTimestampDate(timestamp: Long): String{
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp

        return DateFormat.format("dd/MM/yyyy", calendar).toString()

    }
}