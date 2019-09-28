package com.example.weatherapp.api

import com.example.weatherapp.Const
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {

    private val instance = Retrofit.Builder()
        .baseUrl(Const.WEATHER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getWeatherService() = instance.create(WeatherService::class.java)

}