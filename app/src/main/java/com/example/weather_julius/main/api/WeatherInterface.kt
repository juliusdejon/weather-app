package com.example.weather_julius.main.api

import com.example.weather_julius.main.models.Weather
import retrofit2.http.GET
import retrofit2.http.Path


interface WeatherInterface {
    @GET("timeline/{lat},{lng}/today?unitGroup=metric&elements=datetime,temp,humidity,feelslike,datetimeEpoch,conditions&include=current&key=BQWHRQY4EH9MNN8C65A2D9RRF&contentType=json")
    suspend fun getWeather(@Path("lat") lat: Double, @Path("lng") lng: Double) : Weather
}
