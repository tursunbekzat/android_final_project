package kz.kbtu.olx.presentation.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.data.datasources.retrofit.RetrofitInstance
import kz.kbtu.olx.presentation.adapters.AdAdapter
import kz.kbtu.olx.presentation.adapters.CategoryAdapter
import kz.kbtu.olx.databinding.FragmentHomeBinding
import kz.kbtu.olx.domain.models.Ad
import kz.kbtu.olx.domain.models.Category
import kz.kbtu.olx.presentation.activities.ui.RvListenerCategory
import kz.kbtu.olx.presentation.activities.ui.sell.LocationPickerActivity
import retrofit2.HttpException
import java.io.IOException
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mContext: Context
    private lateinit var adArrayList: ArrayList<Ad>
    private lateinit var adapterAd: AdAdapter
    private lateinit var locationSp: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var currentAddress = ""
    private var cityName = ""


    override fun onAttach(context: Context) {

        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentHomeBinding.inflate(LayoutInflater.from(mContext), container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        locationSp = mContext.getSharedPreferences("LOCATION_SP", Context.MODE_PRIVATE)
        currentLatitude = locationSp.getFloat("CURRENT_LATITUDE", 0.0f).toDouble()
        currentLongitude = locationSp.getFloat("CURRENT_LONGITUDE", 0.0f).toDouble()
        currentAddress= locationSp.getString("CURRENT_ADDRESS", "")!!

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted, get the current location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Use geocoder to get city name from latitude and longitude
                        getCityName(location.latitude, location.longitude)
                    }
                }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        if (currentLongitude != 0.0 && currentLatitude != 0.0) {

            binding.locationTv.text = currentAddress
        }

        loadCategories()

        loadAds("All")

        getCurrentWeather()

        binding.searchEtt.addTextChangedListener (object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                try {

                    val query = s.toString()
                    adapterAd.filter.filter(query)
                } catch (e: Exception) {

                    Log.e(TAG, "onTextChanged: ", e)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.locationCv.setOnClickListener {

            val intent = Intent(mContext, LocationPickerActivity::class.java)
            locationPickerActivityResultLauncher.launch(intent)
        }
    }


    private fun getCityName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses!!.isNotEmpty()) {
                cityName = addresses.get(0).locality
                // Do something with the city name
                println("Current City: $cityName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun loadCategories(){

        val categoryArrayList = ArrayList<Category>()

        for (i in 0 until Utils.categories.size) {

            val modelCategory = Category(Utils.categories[i], Utils.categoryIcons[i])

            categoryArrayList.add(modelCategory)
        }

        val categoryAdapter = CategoryAdapter(mContext, categoryArrayList, object:
            RvListenerCategory {
            override fun onCategoryClick(modelCategory: Category) {

                val selectedCategory = modelCategory.category
                loadAds(selectedCategory)
            }
        })

        binding.categoryRv.adapter = categoryAdapter
    }


    private val locationPickerActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            val data = result.data

            if (data != null) {

                currentLatitude = data.getDoubleExtra("latitude", 0.0)
                currentLongitude = data.getDoubleExtra("longitude", 0.0)
                currentAddress = data.getStringExtra("address").toString()

                locationSp.edit()
                    .putFloat("CURRENT_LATITUDE", currentLatitude.toFloat())
                    .putFloat("CURRENT_LONGITUDE", currentLongitude.toFloat())
                    .putString("CURRENT_ADDRESS", currentAddress)
                    .apply()

                binding.locationTv.text = currentAddress

                loadAds("All")
            }
        } else {

            Utils.toast(mContext, "Cancelled!")
        }
    }


    private fun loadAds(category: String) {

        adArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Ads")
        ref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                adArrayList.clear()

                for (ds in snapshot.children) {

                    try {

                        val ad = ds.getValue(Ad::class.java)

                        val distance = calculateDistanceKm(
                            ad?.latitude ?: 0.0,
                            ad?.longitude ?: 0.0
                        )

                        if (category == "All") {

                            if (distance <= MAX_DISTANCE_TO_LOAD_ADS_KM) {

                                adArrayList.add(ad!!)
                            }
                        } else {

                            if (ad!!.category.equals(category)) {

                                if (distance <= MAX_DISTANCE_TO_LOAD_ADS_KM) {

                                    adArrayList.add(ad)
                                }
                            }

                        }
                    } catch (e: Exception) {

                        Log.e(TAG, "onDataChange: ", e)
                    }
                }

                adapterAd = AdAdapter(mContext, adArrayList)
                binding.adsRv.adapter = adapterAd
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun calculateDistanceKm(adLatitude: Double, adLongitude: Double) : Double {


        val startPoint = Location(LocationManager.NETWORK_PROVIDER)
        startPoint.latitude = currentLatitude
        startPoint.longitude = currentLongitude

        val endPoint = Location(LocationManager.NETWORK_PROVIDER)
        endPoint.latitude = adLatitude
        endPoint.longitude = adLongitude

        val distanceInMeters = startPoint.distanceTo(endPoint).toDouble()

        return distanceInMeters/1000
    }


    private fun getCurrentWeather(){

        getCityName(currentLatitude, currentLongitude)
        GlobalScope.launch(Dispatchers.IO) {

            Log.d(TAG, "getCurrentWeather: $cityName")
            val response = try {

                RetrofitInstance.api.getCurrentWeather("$cityName", "metric", requireContext().getString(R.string.apiKey))
            } catch (e: IOException){

                Utils.toast(mContext, "app error ${e.message}")
                return@launch
            } catch (e: HttpException){

                Utils.toast(mContext, "http error ${e.message}")
                return@launch
            }

            if (response.isSuccessful && response.body()!=null){

                withContext(Dispatchers.Main){

                    val data = response.body()!!
                    val iconId = data.weather[0].icon
                    val imgUrl = "https://api.openweathermap.org/img/w/$iconId.png"

                    Picasso.get().load(imgUrl).into(binding.weatherIv)
                    binding.weatherTv.text = "$cityName ${response.body()!!.main.temp}"
                }
            }
        }
    }


    private companion object {

        private const val TAG = "HOME_FRAGMENT_TAG"
        private const val MAX_DISTANCE_TO_LOAD_ADS_KM = 10
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}