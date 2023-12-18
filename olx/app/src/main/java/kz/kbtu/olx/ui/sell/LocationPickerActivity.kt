package kz.kbtu.olx.ui.sell

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.ActivityLocationPickerBinding
import kotlin.math.log

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityLocationPickerBinding

    private var mMap: GoogleMap? = null
    private var mPlacesClient: PlacesClient? = null
    private var mFusedLocationProviderClient:FusedLocationProviderClient? = null
    private var mLastKnownLocation: Location? = null
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null
    private var selectedAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLocationPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.doneLl.visibility = View.GONE

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Places.initialize(this, getString(R.string.maps_api_key))

        mPlacesClient = Places.createClient(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val autocompleteSupportMapFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        val placeList = arrayOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        autocompleteSupportMapFragment.setPlaceFields(listOf(*placeList))
        autocompleteSupportMapFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(place: Place) {

                val id = place.id
                val name = place.name
                val latLng = place.latLng
                selectedLatitude = latLng?.latitude
                selectedLongitude = latLng?.longitude
                selectedAddress = place.address ?: ""

                if (name != null) addMarker(latLng, name, selectedAddress)
            }

            override fun onError(status: Status) {}
        })

        setOnClickListeners()
    }


    private fun setOnClickListeners(){

        binding.toolbarBackBtn.setOnClickListener {

            onBackPressed()
        }

        binding.toolbarGpsBtn.setOnClickListener {

            if (isGpsEnabled()) {

                requestLocationPermissions.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {

                Utils.toast(this, "Location is not on! Turn it on to show current location")
            }
        }

        binding.doneBtn.setOnClickListener {

            val intent = Intent()
            intent.putExtra("latitude", selectedLatitude)
            intent.putExtra("longitude", selectedLongitude)
            intent.putExtra("address", selectedAddress)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    private fun detectAndShowDeviceLocationMap(){

        try {

            val locationResult = mFusedLocationProviderClient!!.lastLocation
            locationResult.addOnSuccessListener { location ->

                if (location != null) {

                    mLastKnownLocation = location
                    selectedLatitude = location.latitude
                    selectedLongitude = location.longitude

                    val latLng = LatLng(selectedLatitude!!, selectedLongitude!!)
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat()))
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM.toFloat()))

                    addressFromLatLng(latLng)
                }
            }.addOnFailureListener { e ->

                Log.e(TAG, "detectAndShowDeviceLocationMap: ", e)
            }
        } catch (e: Exception) {

            Log.e(TAG, "detectAndShowDeviceLocationMap: ", e)
        }
    }


    private fun isGpsEnabled(): Boolean {

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {

            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {

            Log.e(TAG, "isGpsEnabled: ", e)
        }
        try {

            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {

            Log.e(TAG, "isGpsEnabled: ", e)
        }

        return !(!gpsEnabled && !networkEnabled)
    }


    @SuppressLint("MissingPermission")
    private val requestLocationPermissions:ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            if (isGranted) {

                mMap!!.isMyLocationEnabled = true
                pickCurrentPlace()
            } else {

                Utils.toast(this, "Permission denied")
            }
        }


    private fun pickCurrentPlace() {

        if (mMap == null) return

        detectAndShowDeviceLocationMap()
    }

    private fun addMarker(latLng: LatLng?, title: String, selectedAddress: String) {

        mMap!!.clear()

        try {

            val markerOptions = MarkerOptions()
            if (latLng != null) {
                markerOptions.position(latLng)
            }
            markerOptions.title(title)
            markerOptions.snippet(selectedAddress)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            mMap!!.addMarker(markerOptions)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, DEFAULT_ZOOM.toFloat()))

            binding.doneLl.visibility = View.VISIBLE
            binding.selectedPlaceTv.text = selectedAddress
        } catch (e: Exception) {

            Log.e(TAG, "addMarker: ", e)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        requestLocationPermissions.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        mMap!!.setOnMapClickListener { latLng ->

            selectedLatitude = latLng.latitude
            selectedLongitude = latLng.longitude
            addressFromLatLng(latLng)
        }
    }


    private fun addressFromLatLng(latLng: LatLng) {

        val geocoder = Geocoder(this)

        try {

            val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val address = addressList!![0]
            val addressLine = address.getAddressLine(0)
            val subLocality = address.subLocality

            selectedAddress = addressLine

            addMarker(latLng, subLocality, addressLine)
        } catch (e: Exception) {

            Log.e(TAG, "addressFromLatLng: ", e)
        }
    }


    private companion object {

        private const val TAG = "LOCATION_PICKER_TAG"
        private const val DEFAULT_ZOOM = 15
    }
}
