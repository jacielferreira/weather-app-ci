package com.example.weatherapp.feature.list

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.Const
import com.example.weatherapp.R
import com.example.weatherapp.api.RetrofitManager
import com.example.weatherapp.database.RoomManager
import com.example.weatherapp.entity.City
import com.example.weatherapp.entity.FavoriteCity
import com.example.weatherapp.entity.FindResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row_city_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.Context




class ListAdapter () : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var items : List<City>? = null
    private var context = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_city_layout, parent, false)



        return ViewHolder(view)
    }

    override fun getItemCount() = items?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items?.let {
            val city = it[position]
            holder.itemView.apply {
                if(city.weatherList.size > 0) {
                    tvCloudState.text = city.weatherList[0].description
                    tvWind.text = city.wind.speed.toString()+" m/s"
                    tvCloud.text = city.clouds.all.toString()+"%"
                    tvHpa.text = city.main.pressure.toString()+" hpa"
                    Glide.with(this.context)
                        .load("http://openweathermap.org/img/w/${city.weatherList[0].icon}.png")
                        .into(imgIcon)
                }
                tvTemperature.text = city.main.temp.toString()
                tvCityName.text = city.name

                val roomManager = RoomManager.instance(context)

                val cityById: FavoriteCity = roomManager.getFavoriteDao()
                    .getFavoriteCityById(city.id)

                if(cityById != null) {
                    Log.d("felipedeev", cityById.name)
                    Log.d("felipedeev", cityById.id.toString())

                    btnAddFav.setBackgroundResource(R.drawable.ic_star_fav)
                } else {
                    btnAddFav.setBackgroundResource(R.drawable.ic_star_nofav)
                }

                // CLICK BTN FAVORITE
                btnAddFav.setOnClickListener {
                    val roomManager = RoomManager.instance(context)

                    val cityResult: FavoriteCity = roomManager.getFavoriteDao().getFavoriteCityById(city.id)

                    if(cityResult == null) {
                        roomManager.getFavoriteDao()
                        .addCity(FavoriteCity(city.id, city.name))
                        it.setBackgroundResource(R.drawable.ic_star_fav)
                    } else {
                        roomManager.getFavoriteDao()
                        .delete(cityResult)

                        val list: List<FavoriteCity>? = roomManager.getFavoriteDao()
                            .getFavoriteCities()

                        val query: ArrayList<String> = ArrayList<String>()
                        list?.forEach {
                            query.add(it.id.toString())
                        }
                        val result: String = TextUtils.join(",", query)


                        var sp : SharedPreferences = it.context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                        val temp = sp.getString("temperature", "metric")
                        val language = sp.getString("language", "pt")
                        val url = Const.WEATHER_BASE_URL+"group?units=${temp}&lang=${language}&"

                        updateFavList(url, result, Const.WEATHER_API_KEY)
                    }
                }
            }
        }
    }

    fun data(items: List<City>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun updateFavList(url: String, query: String, key: String) {

        val call = RetrofitManager
            .getWeatherService()
            .groupDynamic(url, query, key)

        call.enqueue(object : Callback<FindResult> {
            override fun onFailure(call: Call<FindResult>, t: Throwable) {
                Log.d("felipedev", "ERROR", t)
            }

            override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
                println(response.message())
                if (response.isSuccessful) {
                    val result = response.body()
                    result.let {
                        data(it!!.items)
                    }
                }
            }

        })
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}