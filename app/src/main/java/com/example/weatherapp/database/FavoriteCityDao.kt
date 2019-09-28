package com.example.weatherapp.database

import androidx.room.*
import com.example.weatherapp.entity.FavoriteCity


@Dao
interface FavoriteCityDao {

    @Query("SELECT * FROM TB_CITY")
    fun getFavoriteCities() : List<FavoriteCity>

    @Query("SELECT * FROM TB_CITY WHERE ID = :id")
    fun getFavoriteCityById(id: Int) : FavoriteCity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCity(favoriteCity: FavoriteCity)

    @Delete
    fun delete(favoriteCity: FavoriteCity)

}