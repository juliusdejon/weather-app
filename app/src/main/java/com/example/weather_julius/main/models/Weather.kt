package com.example.weather_julius.main.models

data class Weather (
    val queryCost: Long,
    val latitude: Double,
    val longitude: Double,
    val resolvedAddress: String,
    val address: String,
    val timezone: String,
    val tzoffset: Long,
    val days: List<Day>,
    val currentConditions: CurrentConditions,
)

data class Day(
    val datetime: String,
    val temp: Double,
    val conditions: String,
    val feelslike: Double,
    val humidity: Double,
)

data class CurrentConditions(
    val datetime: String,
    val temp: Double,
    val conditions: String,
    val feelslike: Double,
    val humidity: Double,
    val datetimeEpoch: Long,
)
