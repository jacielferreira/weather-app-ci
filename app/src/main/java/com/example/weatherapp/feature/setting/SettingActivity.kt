package com.example.weatherapp.feature.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.feature.list.ListActivity
import com.example.weatherapp.R
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.custom_setting_top_bar.view.*





class SettingActivity : AppCompatActivity() {

    private val sharedPref by lazy {
        getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initUI()
    }

    fun initUI() {
        setSupportActionBar(includeToolbar.toolbar)
        includeToolbar.tvToolbarTitle.text = getString(R.string.title_setting)
        includeToolbar.btnBack.setOnClickListener {
            finish()
        }

        // IMPLEMENTAR CONDIÇÃO PARA BUSCAR O SHARED Q JA FOI PERSISTIDO
        val temp = sharedPref.getString("temperature", "metric")
        val language = sharedPref.getString("language", "pt")

        if (temp == "metric") {
            rbCelsius.isChecked = true
            rbFahrenheit.isChecked = false
        } else {
            rbCelsius.isChecked = false
            rbFahrenheit.isChecked = true
        }

        if (language == "pt") {
            rbPortuguese.isChecked = true
            rbEnglish.isChecked = false
        } else {
            rbPortuguese.isChecked = false
            rbEnglish.isChecked = true
        }
    }

    fun saveClick(v: View) {

        sharedPref.edit().apply {
            if (rbCelsius.isChecked) {
                putString("temperature", "metric")
            } else {
                putString("temperature", "imperial")
            }

            if (rbPortuguese.isChecked) {
                putString("language", "pt")
            } else {
                putString("language", "en")
            }
            apply()
        }
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
        Toast.makeText(applicationContext,"Settings was saved with success !",
            Toast.LENGTH_SHORT).show()
    }
}
