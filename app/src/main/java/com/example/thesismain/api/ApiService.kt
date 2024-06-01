package com.example.thesismain.api

import com.example.thesismain.models.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // User registration
    @POST("api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    // User login
    @POST("api/auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    // Get user profile
    @GET("api/auth/profile")
    fun getUserProfile(@Header("Authorization") token: String): Call<User>

    // Update user profile
    @PUT("api/auth/profile")
    fun updateUserProfile(@Header("Authorization") token: String, @Body user: User): Call<User>

    // Upload profile picture
    @Multipart
    @POST("api/users/profilePicture")
    fun uploadProfilePicture(
        @Header("Authorization") token: String,
        @Part profilePicture: MultipartBody.Part
    ): Call<User>

    // Update user vehicle
    @PUT("api/vehicles/updateByToken")
    fun updateVehicleByToken(
        @Header("Authorization") token: String,
        @Body vehicle: Vehicle
    ): Call<Vehicle>

    // Register vehicle
    @POST("api/vehicles/register")
    fun registerVehicle(@Header("Authorization") token: String, @Body vehicle: VehicleInformation): Call<Vehicle>

    // Get vehicle details
    @GET("api/vehicles")
    fun getVehicle(@Header("Authorization") token: String): Call<List<Vehicle>>

//    @PUT("api/vehicles")
//    fun updateVehicle(@Header("Authorization") token: String, @Body vehicle: Vehicle): Call<Vehicle>

    // Get most recent smoke test
//    @GET("api/smokeTests")
//    fun getSmokeTest(@Header("Authorization") token: String): Call<List<SmokeTest>>

//    @FormUrlEncoded
//    @POST("api/smoke-test")
//    fun sendSmokeTestData(
//        @Field("opacity") opacity: String,
//        @Field("smoke_result") smokeResult: String
//    ): Call<ResponseBody>

    // Post Opacity data to DB
    @POST("api/smokeTests")
    fun sendOpacityData(
        @Header("Authorization") token: String,
        @Body data: Map<String, String>
    ): Call<ResponseBody>

//    // Auth for SMoketest to make use data will be send to the right user
//    @POST("/api/smokeTests")
//    fun createSmokeTest(
//        @Header("Authorization") token: String,
//        @Body smokeTestData: HashMap<String, Any>
//    ): Call<ResponseBody>

    // Get smoke test history
    @GET("api/smokeTests/history")
    fun getHistory(@Header("Authorization") token: String): Call<List<SmokeTest>>
}
