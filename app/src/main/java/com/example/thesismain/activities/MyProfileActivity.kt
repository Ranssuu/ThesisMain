package com.example.thesismain.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.thesismain.R
import com.example.thesismain.api.ApiClient
import com.example.thesismain.api.ApiService
import com.example.thesismain.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfileActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var numberTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var editButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        // Initialize views
        firstNameTextView = findViewById(R.id.firstName)
        lastNameTextView = findViewById(R.id.lastName)
        emailTextView = findViewById(R.id.email)
        numberTextView = findViewById(R.id.number)
        passwordTextView = findViewById(R.id.password)
        profileImageView = findViewById(R.id.profileImageView)
        editButton = findViewById(R.id.edit)

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        // Fetch user profile
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
            val call = apiService.getUserProfile("Bearer $token")
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        user?.let {
                            populateUserProfile(it)
                        }
                    } else {
                        Toast.makeText(this@MyProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("MyProfileActivity", "Error: ${t.message}")
                    Toast.makeText(this@MyProfileActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateUserProfile(user: User) {
        firstNameTextView.text = user.firstName
        lastNameTextView.text = user.lastName
        emailTextView.text = user.email
        numberTextView.text = user.phoneNumber
        passwordTextView.text = "********" // Mask the password

        if (user.profilePictureUrl != null) {
            Glide.with(this)
                .load("http://yourserveraddress/${user.profilePictureUrl}")
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.profilepic)
        }
    }

    fun onEditClicked(view: View) {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("firstName", firstNameTextView.text.toString())
        intent.putExtra("lastName", lastNameTextView.text.toString())
        intent.putExtra("email", emailTextView.text.toString())
        intent.putExtra("phoneNumber", numberTextView.text.toString())
        startActivity(intent)
    }


    fun onBackPressed(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}