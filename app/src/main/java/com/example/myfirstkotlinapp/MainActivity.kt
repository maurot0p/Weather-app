package com.example.myfirstkotlinapp

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    var BaseUrl = "http://api.openweathermap.org/"
    var AppId = "a90f8bc6c0afe26429ee153cf3282b94"
    val PERMISSION_ID = 141
    var lon: Int = 0
    var lat: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.maincontainer).visibility = View.GONE
        findViewById<TextView>(R.id.errortext).visibility = View.GONE
        getLocation()
    }

    private fun getCurrentData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            var response = service.getCurrentWeatherData(lat, lon, AppId)
            runOnUiThread {
                if (response.isSuccessful) {
                    val updatedAt: Long = response.body()!!.dt.toLong()
                    val updatedAtText = "Updated at: " + SimpleDateFormat(
                        "dd/MM/yyyy hh:mm a",
                        Locale.ENGLISH
                    ).format(Date(updatedAt * 1000))
                    findViewById<TextView>(R.id.updated_at).text = updatedAtText
                    findViewById<TextView>(R.id.address).text =
                        response.body()!!.name + ", " + response.body()!!.sys.country
                    findViewById<TextView>(R.id.status).text =
                        response.body()!!.weather[0].description.toString().capitalize()
                    findViewById<TextView>(R.id.temp).text =
                        response.body()!!.main.temp.toString() + "°C"
                    findViewById<TextView>(R.id.temp_min).text =
                        "Min Temp: " + response.body()!!.main.temp_min.toString() + "°C"
                    findViewById<TextView>(R.id.temp_max).text =
                        "Max Temp: " + response.body()!!.main.temp_max.toString() + "°C"
                    findViewById<TextView>(R.id.wind).text = response.body()!!.wind.speed.toString()
                    findViewById<TextView>(R.id.pressure).text =
                        response.body()!!.main.pressure.toString()
                    findViewById<TextView>(R.id.humidity).text =
                        response.body()!!.main.humidity.toString()
                    findViewById<TextView>(R.id.sunrise).text =
                        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                            Date(
                                (response.body()!!.sys.sunrise * 1000).toLong()
                            )
                        )
                    findViewById<TextView>(R.id.sunrise).text =
                        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                            Date(
                                (response.body()!!.sys.sunset * 1000).toLong()
                            )
                        )
                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<RelativeLayout>(R.id.maincontainer).visibility = View.VISIBLE
                } else {
                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<TextView>(R.id.errortext).visibility = View.VISIBLE
                }
            }


        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0f, this)
    }
    override fun onLocationChanged(location: Location) {
        lat=location.latitude.toInt()
        lon=location.longitude.toInt()
        getCurrentData()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}








