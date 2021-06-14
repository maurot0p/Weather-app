package com.example.myfirstkotlinapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather?units=metric")
    suspend fun getCurrentWeatherData(@Query("lat") lat: Int?, @Query("lon") lon: Int?, @Query("APPID") app_id: String):Response<WeatherResponseX>
}