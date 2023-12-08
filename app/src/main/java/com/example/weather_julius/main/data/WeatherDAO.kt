package com.example.weather_julius.main.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface WeatherDAO {

    @Insert
    fun insertWeather(newWeather: Weather)

    @Delete
    fun deleteWeather(note : Weather)

    @Query("SELECT * FROM table_weather")
    fun getAllWeather() : LiveData<List<Weather>>

}