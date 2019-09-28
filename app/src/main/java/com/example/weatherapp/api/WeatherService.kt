package com.example.weatherapp.api

import com.example.weatherapp.entity.FindResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeatherService {

    @GET
    fun find(@Url url: String,
        @Query(value = "q") cityName: String,
        @Query(value = "appid") appId: String) : Call<FindResult>

    @GET
    fun groupDynamic(@Url url: String,
              @Query(value = "id") cityIds: String,
              @Query(value = "appid") appId: String) : Call<FindResult>

    @GET(value = "group")
    fun group(
        @Query(value = "id") cityIds: String,
        @Query(value = "appid") appId: String) : Call<FindResult>

}