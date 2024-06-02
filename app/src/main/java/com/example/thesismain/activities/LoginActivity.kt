package com.example.thesismain.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thesismain.R
import com.example.thesismain.api.ApiClient
import com.example.thesismain.api.ApiService
import com.example.thesismain.models.LoginRequest
import com.example.thesismain.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.signin)
        registerTextView = findViewById(R.id.register)

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        loginButton.setOnClickListener {
            loginUser()
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = LoginRequest(email, password)
        val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
        val call = apiService.loginUser(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        with(sharedPreferences.edit()) {
                            putString("auth_token", it.token)
                            apply()
                        }
                        Log.d("LoginActivity", "Token stored: ${it.token}")
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Error: ${t.message}")
                Toast.makeText(this@LoginActivity, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
