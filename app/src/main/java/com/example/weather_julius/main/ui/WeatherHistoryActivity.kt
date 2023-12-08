package com.example.weather_julius.main.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_julius.R
import com.example.weather_julius.databinding.ActivityWeatherHistoryBinding
import com.example.weather_julius.main.data.Weather
import com.example.weather_julius.main.data.WeatherRepository
import com.example.weather_julius.main.ui.adapters.WeatherAdapter
import kotlinx.coroutines.launch

class WeatherHistoryActivity : AppCompatActivity() {
    private val TAG = "WEATHER_HISTORY"
    private lateinit var binding: ActivityWeatherHistoryBinding
    private lateinit var adapter: WeatherAdapter
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherList: MutableList<Weather>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.weatherRepository = WeatherRepository(application)

        weatherList = mutableListOf()
        adapter = WeatherAdapter(weatherList)
        binding.rvItems.adapter = adapter
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    override fun onStart() {
        super.onStart()

        //initiate the observer for live data for allNotes
        this.weatherRepository.allWeather?.observe(this) { weather ->
            if (weather.isNotEmpty()) {
                Log.d(TAG, "onStart: ReceivedNotes : $weather")
                weatherList.clear()
                weatherList.addAll(weather)
                adapter.notifyDataSetChanged()

            } else {
                Log.d(TAG, "onStart: No data received from observer")
            }
        }
    }
}