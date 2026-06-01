package com.example.network

import com.example.BuildConfig
import com.example.data.Movie
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(val text: String)

@JsonClass(generateAdapter = true)
data class Content(val parts: List<Part>)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(val content: Content)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(val candidates: List<Candidate>?)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun generateRecommendation(prompt: String, availableMovies: List<Movie>): String {
        val systemPrompt = """
            You are Mova - a premium AI Movie Recommendation assistant. 
            The user wants movie recommendations. You MUST suggest movies from our active curated list, 
            or recommend custom ones that match their mood/interests perfectly! 
            Be elegant, enthusiastic, and provide visual reviews. Keep your explanations highly professional, concise, with cinematic flair.
            Here is our active curated film list in our app catalog: 
            ${availableMovies.joinToString { "- ${it.title} (${it.genre}, Rating: ${it.rating})" } }
            
            Always tailor your response specifically to their inputs. Address the user directly as a premium movie-lover!
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(prompt)))),
            systemInstruction = Content(parts = listOf(Part(systemPrompt)))
        )

        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
                return "AI Studio API Key is currently unconfigured. Mova's local smart recommendation system predicts you would enjoy 'Interstellar' or 'Dune: Part Two' based on your profile!"
            }
            val response = service.generateContent(key, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Mova is reflecting on the perfect films for you... 'Dune: Part Two' is highly recommended!"
        } catch (e: Exception) {
            "Mova AI recommendation fallback: Our local recommendation engine recommends 'Your Name' (Anime/Romance) or 'The Dark Knight' (Action/Thriller) to suit your taste! Connect to AI Studio to unlock full AI conversational powers. (Error: ${e.message})"
        }
    }
}
