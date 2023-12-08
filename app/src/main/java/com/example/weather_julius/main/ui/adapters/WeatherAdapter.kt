package com.example.weather_julius.main.ui.adapters

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_julius.R
import com.example.weather_julius.main.data.Weather
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherAdapter (
    private val weatherList:List<Weather>
): RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {
    private var TAG = "WEATHER_ADAPTER"
    private lateinit var context: Context
    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_weather, parent, false)
        context = parent.context
        return WeatherViewHolder(view)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val currWeather: Weather = weatherList.get(position)

        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val searchResults: MutableList<Address>? =  geocoder.getFromLocation(currWeather.latitude, currWeather.longitude, 1)
            if (searchResults == null) {
                Log.d(TAG, "ERROR: When retrieving results")
            }
            if (searchResults != null) {
                if (searchResults.size > 0) {
                    Log.d(TAG, "Search results found")
                    val addressObject:Address = searchResults.get(0)
                    val tvTitle = holder.itemView.findViewById<TextView>(R.id.tvTitle)
                    tvTitle.text = addressObject.locality
                }
            }
        } catch (exception:Exception) {
            Log.d(TAG, "Exception occurred while getting matching address")
            Log.d(TAG, exception.toString())
        }

        val localDateTime = convertTimestampToFormattedDate(currWeather.datetimeEpoch)
        val tvDate = holder.itemView.findViewById<TextView>(R.id.tvDate)
        tvDate.text = localDateTime

        val tvCondition = holder.itemView.findViewById<TextView>(R.id.tvCondition)
        tvCondition.text = "Condition: ${currWeather.conditions} | Humidity ${currWeather.humidity}%"

        val tvFeelsLike = holder.itemView.findViewById<TextView>(R.id.tvFeelsLike)
        tvFeelsLike.text = "Feels like ${currWeather.feelslike}°"

        val tvTemp = holder.itemView.findViewById<TextView>(R.id.tvTemp)
        tvTemp.text = "${currWeather.temp}°"
    }
    private fun convertTimestampToFormattedDate(timestamp: Long): String {
        val date = Date(timestamp * 1000L) // Convert seconds to milliseconds
        val dateFormat = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
        return dateFormat.format(date)
    }
}
