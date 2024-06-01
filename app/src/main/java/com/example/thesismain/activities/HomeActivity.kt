package com.example.thesismain.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.thesismain.R

class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        checkIfLoggedIn()
        logStoredData()
    }

    private fun checkIfLoggedIn() {
        val token = sharedPreferences.getString("auth_token", null)
        if (token == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun logStoredData() {
        val token = sharedPreferences.getString("auth_token", null)
        val ownerId = sharedPreferences.getString("ownerId", null)

        Log.d("HomeActivity", "Token: $token")
        Log.d("HomeActivity", "Owner ID: $ownerId")
    }

    fun onEmissionClicked(view: View) {
        val intent = Intent(this, EmissionActivity::class.java)
        startActivity(intent)
    }

    fun onHistoryClicked(view: View) {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    fun onSettingsClicked(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}
