package com.example.thesismain.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.thesismain.R
import com.example.thesismain.api.ApiClient
import com.example.thesismain.api.ApiService
import com.example.thesismain.models.SmokeTest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var historyContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyContainer = findViewById(R.id.history_container)
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        fetchSmokeTestHistory()
    }

    private fun fetchSmokeTestHistory() {
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
            val call = apiService.getHistory("Bearer $token")
            call.enqueue(object : Callback<List<SmokeTest>> {
                override fun onResponse(call: Call<List<SmokeTest>>, response: Response<List<SmokeTest>>) {
                    if (response.isSuccessful) {
                        val smokeTests = response.body()
                        smokeTests?.let {
                            populateHistory(it)
                        }
                    } else {
                        Toast.makeText(this@HistoryActivity, "Failed to load history", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<SmokeTest>>, t: Throwable) {
                    Log.e("HistoryActivity", "Error: ${t.message}")
                    Toast.makeText(this@HistoryActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateHistory(smokeTestHistory: List<SmokeTest>) {
        historyContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)

        smokeTestHistory.forEach { smokeTest ->
            val historyWidget = inflater.inflate(R.layout.historywidget, historyContainer, false)

            val opacityTextView: TextView = historyWidget.findViewById(R.id.progress_text)
            val opacityDecimalTextView: TextView = historyWidget.findViewById(R.id.opacity_decimal_text)
            val resultTextView: TextView = historyWidget.findViewById(R.id.result_text)
            val dateCreatedTextView: TextView = historyWidget.findViewById(R.id.dateCreatedText)
            val progressBar: ProgressBar = historyWidget.findViewById(R.id.progress_bar)

            // Set the opacity value and progress bar
            val opacityPercentage = ((smokeTest.opacity/3.0) * 100).toInt()
            opacityTextView.text = "$opacityPercentage%"
            opacityDecimalTextView.text = smokeTest.opacity.toString()
            progressBar.progress = opacityPercentage

            // Set the smoke test result
            resultTextView.text = smokeTest.smoke_result

            // Format the date
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val targetFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
            val date = originalFormat.parse(smokeTest.createdAt)
            val formattedDate = targetFormat.format(date)
            dateCreatedTextView.text = formattedDate

            // Determine colors based on opacity value
            val passedColor = ContextCompat.getColor(this, R.color.passedColor)
            val failedColor = ContextCompat.getColor(this, R.color.failedColor)

            if (opacityPercentage > 80) {
                resultTextView.text = "Fail"
                resultTextView.setTextColor(failedColor)
                opacityTextView.setTextColor(failedColor)
                opacityDecimalTextView.setTextColor(failedColor)
                progressBar.progressDrawable.setTint(failedColor)
            } else {
                resultTextView.text = "Pass"
                resultTextView.setTextColor(passedColor)
                opacityTextView.setTextColor(passedColor)
                opacityDecimalTextView.setTextColor(passedColor)
                progressBar.progressDrawable.setTint(passedColor)
            }

            // Add the history widget to the container
            historyContainer.addView(historyWidget)
        }
    }

    fun onSettingsClicked(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onHomeClicked(view: View) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onEmissionClicked(view: View) {
        val intent = Intent(this, EmissionActivity::class.java)
        startActivity(intent)
        finish()
    }
}
