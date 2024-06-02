package com.example.thesismain.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thesismain.R
import com.example.thesismain.api.ApiClient
import com.example.thesismain.api.ApiService
import com.example.thesismain.models.User
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var mobileNumberEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var updateProfileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize views
        firstNameEditText = findViewById(R.id.firstname)
        lastNameEditText = findViewById(R.id.lastname)
        emailEditText = findViewById(R.id.email)
        mobileNumberEditText = findViewById(R.id.mobilenumber)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.cpassword)
        updateProfileButton = findViewById(R.id.updateprofile)

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

        // Fetch user details from intent
        val firstName = intent.getStringExtra("firstName")
        val lastName = intent.getStringExtra("lastName")
        val email = intent.getStringExtra("email")
        val mobileNumber = intent.getStringExtra("phoneNumber")

        // Set user details to EditTexts
        firstNameEditText.setText(firstName)
        lastNameEditText.setText(lastName)
        emailEditText.setText(email)
        mobileNumberEditText.setText(mobileNumber)

        updateProfileButton.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun updateUserProfile() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val mobileNumber = mobileNumberEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
            val user = User(
                id = "", // Ensure the correct user ID is passed
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = mobileNumber,
                password = if (password.isNotEmpty()) password else null,
                profilePictureUrl = null
            )
            val call = apiService.updateUserProfile("Bearer $token", user)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val updatedFields = mutableListOf<String>()
                        if (firstName.isNotEmpty() || lastName.isNotEmpty() || email.isNotEmpty() || mobileNumber.isNotEmpty()) {
                            updatedFields.add("Profile")
                        }
                        if (password.isNotEmpty()) {
                            updatedFields.add("Password")
                        }

                        val message = when {
                            updatedFields.size > 1 -> "Profile and Password updated successfully"
                            updatedFields.size == 1 -> "${updatedFields[0]} updated successfully"
                            else -> "Profile updated successfully"
                        }

                        Toast.makeText(this@EditProfileActivity, message, Toast.LENGTH_SHORT).show()

                        // Start MyProfileActivity with updated details
                        val intent = Intent(this@EditProfileActivity, MyProfileActivity::class.java).apply {
                            putExtra("firstName", firstName)
                            putExtra("lastName", lastName)
                            putExtra("email", email)
                            putExtra("phoneNumber", mobileNumber)
                        }
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Profile update failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("EditProfileActivity", "Error: ${t.message}")
                    Toast.makeText(this@EditProfileActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show()
        }
    }
}
