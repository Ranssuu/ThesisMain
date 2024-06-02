package com.example.thesismain.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thesismain.R
import com.example.thesismain.api.ApiClient
import com.example.thesismain.api.ApiService
import com.example.thesismain.models.Vehicle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyVehicleActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var plateNoTextView: TextView
    private lateinit var engineNoTextView: TextView
    private lateinit var chassisNoTextView: TextView
    private lateinit var yearModelTextView: TextView
    private lateinit var makeSeriesTextView: TextView
    private lateinit var mvTypeTextView: TextView
    private lateinit var colorTextView: TextView
    private lateinit var classificationTextView: TextView
    private lateinit var editButton: Button
    private lateinit var backButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_vehicle)

        plateNoTextView = findViewById(R.id.plateno)
        engineNoTextView = findViewById(R.id.engineno)
        chassisNoTextView = findViewById(R.id.chassisno)
        yearModelTextView = findViewById(R.id.yearmodel)
        makeSeriesTextView = findViewById(R.id.makeseries)
        mvTypeTextView = findViewById(R.id.mvtype)
        colorTextView = findViewById(R.id.color)
        classificationTextView = findViewById(R.id.classification)
        editButton = findViewById(R.id.edit)

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        fetchVehicleDetails()

        editButton.setOnClickListener {
            val intent = Intent(this, EditVehicleActivity::class.java).apply {
                putExtra("plateNo", plateNoTextView.text.toString())
                putExtra("engineNo", engineNoTextView.text.toString())
                putExtra("chassisNo", chassisNoTextView.text.toString())
                putExtra("yearModel", yearModelTextView.text.toString())
                putExtra("makeSeries", makeSeriesTextView.text.toString())
                putExtra("mvType", mvTypeTextView.text.toString())
                putExtra("color", colorTextView.text.toString())
                putExtra("classification", classificationTextView.text.toString())
            }
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun fetchVehicleDetails() {
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
            val call = apiService.getVehicle("Bearer $token")
            call.enqueue(object : Callback<List<Vehicle>> {
                override fun onResponse(call: Call<List<Vehicle>>, response: Response<List<Vehicle>>) {
                    if (response.isSuccessful) {
                        val vehicles = response.body()
                        if (vehicles != null && vehicles.isNotEmpty()) {
                            val vehicle = vehicles[0]  // Assuming a user has only one vehicle
                            plateNoTextView.text = vehicle.plateNo
                            engineNoTextView.text = vehicle.engineNo
                            chassisNoTextView.text = vehicle.chassisNo
                            yearModelTextView.text = vehicle.yearModel
                            makeSeriesTextView.text = vehicle.makeSeries
                            mvTypeTextView.text = vehicle.mvType
                            colorTextView.text = vehicle.color
                            classificationTextView.text = vehicle.classification
                        } else {
                            Toast.makeText(this@MyVehicleActivity, "No vehicle details found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MyVehicleActivity, "Failed to load vehicle details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {
                    Log.e("MyVehicleActivity", "Error: ${t.message}")
                    Toast.makeText(this@MyVehicleActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show()
        }
    }

    fun onBackPressed(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
