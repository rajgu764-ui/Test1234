package com.example.network

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

@JsonClass(generateAdapter = true)
data class SupabaseReview(
    val id: String,
    val movie_id: String,
    val user_name: String,
    val rating: Int,
    val comment: String,
    val created_at: String
)

@JsonClass(generateAdapter = true)
data class SupabaseProfile(
    val email: String,
    val user_name: String,
    val favorite_genres: String,
    val is_premium: Boolean
)

object SupabaseApiClient {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val client = OkHttpClient.Builder().build()
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    private val isConfigured: Boolean
        get() {
            val url = BuildConfig.SUPABASE_URL
            val key = BuildConfig.SUPABASE_ANON_KEY
            return url.isNotBlank() && !url.contains("placeholder") && 
                   key.isNotBlank() && !key.contains("placeholder")
        }

    fun getBaseUrl(): String = BuildConfig.SUPABASE_URL

    suspend fun fetchReviews(movieId: String): List<SupabaseReview> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptyList()

        val url = "${BuildConfig.SUPABASE_URL}/rest/v1/reviews?movie_id=eq.$movieId&select=*"
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            .get()
            .build()

        return@withContext try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use emptyList()
                val body = response.body?.string() ?: return@use emptyList()
                val adapter = moshi.adapter(Array<SupabaseReview>::class.java)
                adapter.fromJson(body)?.toList() ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun insertReview(review: SupabaseReview): Boolean = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext false

        val url = "${BuildConfig.SUPABASE_URL}/rest/v1/reviews"
        val json = moshi.adapter(SupabaseReview::class.java).toJson(review)
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            .addHeader("Prefer", "return=minimal")
            .post(requestBody)
            .build()

        return@withContext try {
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginOrSignUp(email: String, isRegister: Boolean): Boolean = withContext(Dispatchers.IO) {
        // Mock auth to allow exploration if Supabase is unconfigured, or run real auth if configured
        if (!isConfigured) return@withContext true

        val endpoint = if (isRegister) "signup" else "token?grant_type=password"
        val url = "${BuildConfig.SUPABASE_URL}/auth/v1/$endpoint"
        
        // Gotrue standard login/signup body format
        val bodyMap = mapOf(
            "email" to email,
            "password" to "TemporaryPassword123!" // Auto pass for simplified user onboarding
        )
        val json = moshi.adapter(Map::class.java).toJson(bodyMap)
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .post(requestBody)
            .build()

        return@withContext try {
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }
}
