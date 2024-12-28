package com.openai.aistudio
import android.util.Log

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.openai.com/"

    private var retrofit: Retrofit? = null // Retrofit instance is nullable to rebuild when needed

    // Setter for the API key
    fun setApiKey(apiKey: String) {

        // Rebuild the Retrofit instance with the new API key
        retrofit = createRetrofitInstance(apiKey)
    }

    // Logging interceptor for debugging network requests
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Function to create a new Retrofit instance
    private fun createRetrofitInstance(apiKey: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient(apiKey))
            .build()
    }

    // Function to create a new OkHttpClient instance
    private fun createOkHttpClient(apiKey: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${apiKey}") // Use the updated API key
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    // Expose the service dynamically
    val service: OpenAIService
        get() = retrofit?.create(OpenAIService::class.java)
            ?: throw IllegalStateException("API Key is not set. Call setApiKey() first.")
}

