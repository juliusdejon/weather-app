package com.example.weather_julius.main.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.weather_julius.main.data.Weather

class WeatherRepository(application: Application) {
    private var db : AppDB? = null
    private var weatherDAO = AppDB.getDB(application)?.weatherDAO()

    var allWeather : LiveData<List<Weather>>? = weatherDAO?.getAllWeather()

    init {
        this.db = AppDB.getDB(application)
    }

    fun insertWeather(weather : Weather){
        AppDB.databaseQueryExecutor.execute {
            this.weatherDAO?.insertWeather(weather)
        }
    }

    fun deleteWeather(weather: Weather){
        AppDB.databaseQueryExecutor.execute {
            this.weatherDAO?.deleteWeather(weather)
        }
    }

}