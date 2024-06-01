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
import com.example.thesismain.models.RegisterRequest
import com.example.thesismain.models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firstNameEditText = findViewById(R.id.firstname)
        lastNameEditText = findViewById(R.id.lastname)
        emailEditText = findViewById(R.id.email)
        phoneNumberEditText = findViewById(R.id.mobilenumber)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.cpassword)
        registerButton = findViewById(R.id.signup)

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phoneNumber = phoneNumberEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val registerRequest = RegisterRequest(firstName, lastName, email, password, phoneNumber)
        val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
        val call = apiService.registerUser(registerRequest)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        val token = registerResponse.token
                        val editor = sharedPreferences.edit()
                        editor.putString("auth_token", token)
                        editor.apply()
                        val intent = Intent(this@RegisterActivity, RegisterVehicleActivity::class.java)
                        intent.putExtra("auth_token", token)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("RegisterActivity", "Error: ${t.message}")
                Toast.makeText(this@RegisterActivity, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
