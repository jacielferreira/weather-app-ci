package com.example.weatherapp.feature.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Const
import com.example.weatherapp.R
import com.example.weatherapp.api.RetrofitManager
import com.example.weatherapp.database.RoomManager
import com.example.weatherapp.entity.FavoriteCity
import com.example.weatherapp.entity.FindResult
import com.example.weatherapp.feature.setting.SettingActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row_city_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListActivity : AppCompatActivity() {

    private val sharedPref by lazy {
        getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    private val adapter = ListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getFavoriteCitiesAsync()
        initUI()
    }

    @SuppressLint("StaticFieldLeak")
    private fun getFavoriteCitiesAsync() {
        val task = object : AsyncTask<Void, Void, List<FavoriteCity>>() {

            override fun onPreExecute() {
                progressBar.visibility = View.VISIBLE
            }

            override fun doInBackground(vararg p0: Void?): List<FavoriteCity> {
                return RoomManager.instance(this@ListActivity)
                    .getFavoriteDao()
                    .getFavoriteCities()
            }

            override fun onPostExecute(result: List<FavoriteCity>?) {
                super.onPostExecute(result)
                val query: ArrayList<String> = ArrayList<String>()
                result?.forEach {
                    query.add(it.id.toString())
                }
                val url: String = createUrlApi("group")
                val result: String = TextUtils.join(",", query)
                adapter.updateFavList(url, result, Const.WEATHER_API_KEY)
                progressBar.visibility = View.GONE
            }
        }
        task.execute()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun initUI() {

        rvCities.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            adapter = this@ListActivity.adapter
        }

        btnSearch.setOnClickListener {
            searchApi()
        }

        etLocation.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0?.length == 0) {
                    getFavoriteCitiesAsync()
                }
            }
        })
    }

    fun searchApi() {
        if(isDeviceConnected()) {
            progressBar.visibility = View.VISIBLE
            // API START
            val url : String = createUrlApi("find")
            val call = RetrofitManager
                .getWeatherService()
                .find(url, etLocation.text.toString(), Const.WEATHER_API_KEY)

            call.enqueue(object : Callback<FindResult> {
                override fun onFailure(call: Call<FindResult>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.d("felipedev", "ERROR", t)
                }

                override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val result = response.body()
                        result.let {
                            adapter.data(it!!.items)
                        }
                    }
                }

            })
        } else {
            Toast.makeText(applicationContext, "Device isnt connected to internet", Toast.LENGTH_SHORT).show()
        }
    }

    fun createUrlApi(type : String): String {
        val temp = sharedPref.getString("temperature", "metric")
        val language = sharedPref.getString("language", "pt")
        return Const.WEATHER_BASE_URL+type+"?units=${temp}&lang=${language}&"
    }

    fun isDeviceConnected(): Boolean {
        val cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo

        return netInfo != null && netInfo.isConnected
    }
}
