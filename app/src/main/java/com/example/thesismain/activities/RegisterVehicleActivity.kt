package com.example.thesismain.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thesismain.R
import com.example.thesismain.api.ApiClient
import com.example.thesismain.api.ApiService
import com.example.thesismain.models.Vehicle
import com.example.thesismain.models.VehicleInformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterVehicleActivity : AppCompatActivity() {

    private lateinit var plateNoEditText: EditText
    private lateinit var engineNoEditText: EditText
    private lateinit var chassisNoEditText: EditText
    private lateinit var yearModelEditText: EditText
    private lateinit var makeSeriesEditText: EditText
    private lateinit var mvTypeEditText: EditText
    private lateinit var colorEditText: EditText
    private lateinit var classificationEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_vehicle)

        plateNoEditText = findViewById(R.id.plateno)
        engineNoEditText = findViewById(R.id.engineno)
        chassisNoEditText = findViewById(R.id.chassisno)
        yearModelEditText = findViewById(R.id.yearmodel)
        makeSeriesEditText = findViewById(R.id.makeseries)
        mvTypeEditText = findViewById(R.id.mvtype)
        colorEditText = findViewById(R.id.color)
        classificationEditText = findViewById(R.id.classification)
        submitButton = findViewById(R.id.submit)

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        val token = intent.getStringExtra("auth_token") ?: ""
        if (token.isNotEmpty()) {
            val editor = sharedPreferences.edit()
            editor.putString("auth_token", token)
            editor.apply()
        }

        submitButton.setOnClickListener {
            registerVehicle()
        }
    }

    private fun registerVehicle() {
        val plateNo = plateNoEditText.text.toString().trim()
        val engineNo = engineNoEditText.text.toString().trim()
        val chassisNo = chassisNoEditText.text.toString().trim()
        val yearModel = yearModelEditText.text.toString().trim()
        val makeSeries = makeSeriesEditText.text.toString().trim()
        val mvType = mvTypeEditText.text.toString().trim()
        val color = colorEditText.text.toString().trim()
        val classification = classificationEditText.text.toString().trim()

        if (plateNo.isEmpty() || engineNo.isEmpty() || chassisNo.isEmpty() || yearModel.isEmpty() || makeSeries.isEmpty() || mvType.isEmpty() || color.isEmpty() || classification.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val vehicleInfo = VehicleInformation(plateNo, engineNo, chassisNo, yearModel, makeSeries, mvType, color, classification)
        val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
        val token = sharedPreferences.getString("auth_token", "") ?: ""

        val call = apiService.registerVehicle("Bearer $token", vehicleInfo)

        call.enqueue(object : Callback<Vehicle> {
            override fun onResponse(call: Call<Vehicle>, response: Response<Vehicle>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterVehicleActivity, "Vehicle registered successfully", Toast.LENGTH_SHORT).show()

                    val editor = sharedPreferences.edit()
                    editor.remove("auth_token")
                    editor.apply()

                    // Redirect to login
                    val intent = Intent(this@RegisterVehicleActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@RegisterVehicleActivity, "Vehicle registration failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Vehicle>, t: Throwable) {
                Log.e("RegisterVehicleActivity", "Error: ${t.message}")
                Toast.makeText(this@RegisterVehicleActivity, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
