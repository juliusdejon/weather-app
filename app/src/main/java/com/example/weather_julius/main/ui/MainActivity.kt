package com.example.weather_julius.main.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.weather_julius.R
import com.example.weather_julius.databinding.ActivityMainBinding
import com.example.weather_julius.main.api.RetrofitInstance
import com.example.weather_julius.main.api.WeatherInterface
import com.example.weather_julius.main.data.Weather
import com.example.weather_julius.main.data.WeatherRepository
import com.google.android.material.snackbar.Snackbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val TAG:String = "MAIN_ACTIVITy"
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var selectedWeather: com.example.weather_julius.main.models.Weather


    // permissions array
    private val APP_PERMISSIONS_LIST = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(this.binding.menuToolbar)

        multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
        this.weatherRepository = WeatherRepository(application)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // GET WEATHER REPORT
        binding.btnGetWeatherReport.setOnClickListener {
            val geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())
            val addressFromUI = binding.etCity.text.toString()
            try {
                val searchResults:MutableList<Address>? = geocoder.getFromLocationName(addressFromUI, 1)
                if (searchResults == null) {
                    Log.e(TAG, "searchResults variable is null")
                    return@setOnClickListener
                }

                if (searchResults.size == 0) {
                } else {
                    val foundLocation: Address = searchResults.get(0)
                    var message = "Coordinates are: ${foundLocation.latitude}, ${foundLocation.longitude} \n" +
                            "city name: ${foundLocation.locality}"
                    Log.d(TAG, message)
                    var api: WeatherInterface = RetrofitInstance.retrofitService

                    lifecycleScope.launch {
                        val weather = api.getWeather(foundLocation.latitude, foundLocation.longitude)
                        Log.d(TAG, "${weather}")
                        binding.tvWeather.text = "It's ${weather.currentConditions.conditions}"
                        binding.tvCelcius.text = "${weather.currentConditions.temp}°"
                        binding.tvFeelsLike.text = "Feels like ${weather.currentConditions.feelslike}°"
                        binding.tvHumidity.text = "Humidity ${weather.currentConditions.humidity.toInt()}%"
                        val localDateTime = convertTimestampToFormattedDate(weather.currentConditions.datetimeEpoch)
                        binding.tvTime.text = "As of ${localDateTime}"
                        selectedWeather = weather
                    }

                }
            } catch(ex:Exception) {
                Log.e(TAG, "Error encountered while getting coordinate location.")
                Log.e(TAG, ex.toString())
            }
        }

        // SAVE WEATHER REPORT

        binding.btnSaveWeatherReport.setOnClickListener {
            Log.d(TAG, "${selectedWeather}")
            lifecycleScope.launch {
                val weather = Weather(
                    selectedWeather.latitude,
                    selectedWeather.longitude,
                    selectedWeather.currentConditions.temp,
                    selectedWeather.currentConditions.conditions,
                    selectedWeather.currentConditions.feelslike,
                    selectedWeather.currentConditions.humidity,
                    selectedWeather.currentConditions.datetimeEpoch,
                )
                weatherRepository.insertWeather(weather)
                var snackbar = Snackbar.make(binding.root, "Added weather for ${selectedWeather.timezone}", Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_item_history-> {
                var intent = Intent(this@MainActivity, WeatherHistoryActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val multiplePermissionsResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
            resultsList ->
        Log.d(TAG, resultsList.toString())
        var allPermissionsGrantedTracker = true
        for (item in resultsList.entries) {
            if (item.key in APP_PERMISSIONS_LIST && item.value == false) {
                allPermissionsGrantedTracker = false
            }
        }
        if (allPermissionsGrantedTracker == true) {
            getDeviceLocation()
        } else {
            var snackbar = Snackbar.make(binding.root, "Some permissions NOT granted", Snackbar.LENGTH_LONG)
            snackbar.show()
            handlePermissionDenied()
        }
    }

    private fun getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location === null) {
                    Log.d(TAG, "Location is null")
                    return@addOnSuccessListener
                }
                val message = "The device is located at: ${location.latitude}, ${location.longitude}"
                Log.d(TAG, message)
                val geocoder:Geocoder = Geocoder(applicationContext, Locale.getDefault())
                try {
                    val searchResults:MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (searchResults == null) {
                        Log.e(TAG, "getting Street Address: searchResults is NULL ")
                        return@addOnSuccessListener;
                    }
                    if (searchResults.size == 0) {
                        Log.d(TAG, "Search results <= 0")
                    } else {
                        val matchingAddress: Address = searchResults.get(0)
                        Log.d(TAG, "${matchingAddress.locality} ${matchingAddress.countryName}")
                        binding.etCity.setText(matchingAddress.locality)
                    }
                } catch (ex: java.lang.Exception) {
                    Log.e(TAG, "Error encountered while getting coordinate location.")
                    Log.e(TAG, ex.toString())
                }
            }
    }

    private fun handlePermissionDenied() {}

    private fun convertTimestampToFormattedDate(timestamp: Long): String {
        val date = Date(timestamp * 1000L) // Convert seconds to milliseconds
        val dateFormat = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
        return dateFormat.format(date)
    }

}