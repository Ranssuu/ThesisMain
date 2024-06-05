package com.example.thesismain.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://192.168.1.6:3000/"

    // Function to create OkHttpClient with token
    private fun createOkHttpClient(token: String?): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)

        // Logging interceptor for debugging purposes
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        clientBuilder.addInterceptor(loggingInterceptor)

        // Authorization interceptor
        token?.let {
            val interceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            clientBuilder.addInterceptor(interceptor)
        }

        return clientBuilder.build()
    }

    // Create Retrofit instance
    private fun createRetrofit(token: String?): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(token))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Public function to get Retrofit instance
    fun getRetrofitInstance(token: String? = null): Retrofit {
        return createRetrofit(token)
    }

    // Public function to get ApiService instance
    fun create(token: String? = null): ApiService {
        return getRetrofitInstance(token).create(ApiService::class.java)
    }

    // Lazy initialized ApiService without token
    val apiService: ApiService by lazy {
        getRetrofitInstance().create(ApiService::class.java)
    }
}
