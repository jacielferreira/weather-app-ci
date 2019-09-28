package com.example.weatherapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class FindResult(
    @SerializedName(value = "list")
    val items: List<City>
)

data class City(
    val id: Int = 0,
    val name: String = "",
    @SerializedName(value = "weather")
    val weatherList : List<Weather>,
    val main : Main,
    val wind : Wind,
    val clouds : Clouds
)

data class Weather(
    val description : String = "",
    val icon : String = ""
)

data class Main(
    val temp : Float,
    val pressure: Float
)

data class Wind(
    val speed : Float
)

data class Clouds(
    val all: Int
)

@Entity(tableName = "tb_city")
data class FavoriteCity(
    @PrimaryKey
    val id : Int = 0,
    val name : String = ""
)