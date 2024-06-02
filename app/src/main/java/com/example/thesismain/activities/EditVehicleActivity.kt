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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditVehicleActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var plateNoEditText: EditText
    private lateinit var engineNoEditText: EditText
    private lateinit var chassisNoEditText: EditText
    private lateinit var yearModelEditText: EditText
    private lateinit var makeSeriesEditText: EditText
    private lateinit var mvTypeEditText: EditText
    private lateinit var colorEditText: EditText
    private lateinit var classificationEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_vehicle)

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

        // Fetch current vehicle details
        fetchVehicleDetails()

        // Set onClickListener for submit button
        submitButton.setOnClickListener {
            updateVehicleDetails()
        }
    }

    private fun fetchVehicleDetails() {
        val token = sharedPreferences.getString("auth_token", null)
        Log.d("EditVehicleActivity", "Token retrieved: $token")
        if (token != null) {
            val apiService = ApiClient.getRetrofitInstance(token).create(ApiService::class.java)
            val call = apiService.getVehicle("Bearer $token")
            call.enqueue(object : Callback<List<Vehicle>> {
                override fun onResponse(call: Call<List<Vehicle>>, response: Response<List<Vehicle>>) {
                    if (response.isSuccessful) {
                        val vehicles = response.body()
                        Log.d("EditVehicleActivity", "Vehicles retrieved: $vehicles")
                        if (vehicles != null && vehicles.isNotEmpty()) {
                            val vehicle = vehicles[0]  // Assuming a user has only one vehicle
                            plateNoEditText.setText(vehicle.plateNo)
                            engineNoEditText.setText(vehicle.engineNo)
                            chassisNoEditText.setText(vehicle.chassisNo)
                            yearModelEditText.setText(vehicle.yearModel)
                            makeSeriesEditText.setText(vehicle.makeSeries)
                            mvTypeEditText.setText(vehicle.mvType)
                            colorEditText.setText(vehicle.color)
                            classificationEditText.setText(vehicle.classification)
                        } else {
                            Toast.makeText(this@EditVehicleActivity, "No vehicle details found", Toast.LENGTH_SHORT).show()
                            Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
                            Log.e("API Error Body", "Error Body: ${response.errorBody()?.string()}")
                        }
                    } else {
                        Toast.makeText(this@EditVehicleActivity, "Failed to load vehicle details", Toast.LENGTH_SHORT).show()
                        Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
                        Log.e("API Error Body", "Error Body: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {
                    Log.e("EditVehicleActivity", "Error: ${t.message}")
                    Toast.makeText(this@EditVehicleActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateVehicleDetails() {
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

        val vehicle = Vehicle(
            plateNo = plateNo,
            engineNo = engineNo,
            chassisNo = chassisNo,
            yearModel = yearModel,
            makeSeries = makeSeries,
            mvType = mvType,
            color = color,
            classification = classification,
            owner = "" // This will be ignored in the backend
        )

        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            val apiService = ApiClient.getRetrofitInstance(token).create(ApiService::class.java)
            val call = apiService.updateVehicleByToken("Bearer $token", vehicle)
            call.enqueue(object : Callback<Vehicle> {
                override fun onResponse(call: Call<Vehicle>, response: Response<Vehicle>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditVehicleActivity, "Vehicle details updated successfully", Toast.LENGTH_SHORT).show()
                        // Start MyVehicleActivity with updated details
                        val intent = Intent(this@EditVehicleActivity, MyVehicleActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("EditVehicleActivity", "Failed to update vehicle details: ${response.errorBody()?.string()}")
                        Toast.makeText(this@EditVehicleActivity, "Failed to update vehicle details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Vehicle>, t: Throwable) {
                    Log.e("EditVehicleActivity", "Error: ${t.message}")
                    Toast.makeText(this@EditVehicleActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show()
        }
    }
}
