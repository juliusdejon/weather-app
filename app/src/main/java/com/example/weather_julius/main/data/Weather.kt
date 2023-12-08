package com.example.weather_julius.main.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entity
@Entity(tableName = "table_weather")
class Weather(
    var latitude: Double,
    var longitude: Double,
    var temp: Double,
    var conditions: String,
    var feelslike: Double,
    var humidity: Double,
    var datetimeEpoch: Long,
) {

    @PrimaryKey(autoGenerate = true)
    var id = 0
}
