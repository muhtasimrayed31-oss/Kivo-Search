package com.example.network

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Network Models ---

@JsonClass(generateAdapter = true)
data class IpResponse(
    val status: String?,
    val country: String?,
    val city: String?,
    val lat: Double?,
    val lon: Double?
)

@JsonClass(generateAdapter = true)
data class WeatherData(
    val temperature: Double?,
    val windspeed: Double?,
    val weathercode: Int?
)

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val current_weather: WeatherData?
)

@JsonClass(generateAdapter = true)
data class WikiThumbnail(
    val source: String?
)

@JsonClass(generateAdapter = true)
data class WikiResponse(
    val title: String?,
    val extract: String?,
    val thumbnail: WikiThumbnail?,
    val type: String?
)

@JsonClass(generateAdapter = true)
data class SearchItem(
    val title: String?,
    val link: String?,
    val snippet: String?
)

@JsonClass(generateAdapter = true)
data class GoogleSearchResponse(
    val items: List<SearchItem>?
)

// --- Gemini Models ---

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

// --- Retrofit Interfaces ---

interface IpApiService {
    @GET("json")
    suspend fun getIpLocation(): IpResponse
}

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") currentWeather: Boolean = true
    ): WeatherResponse
}

interface WikiApiService {
    @GET("api/rest_v1/page/summary/{title}")
    suspend fun getWikiSummary(
        @Path("title") title: String
    ): WikiResponse
}

interface GoogleSearchApiService {
    @GET("customsearch/v1")
    suspend fun getSearchResults(
        @Query("key") apiKey: String,
        @Query("cx") cx: String,
        @Query("q") query: String,
        @Query("start") start: Int = 1,
        @Query("searchType") searchType: String? = null
    ): GoogleSearchResponse
}

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// --- Retrofit Client Setup ---

object KivoNetwork {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val ipService: IpApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://ip-api.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(IpApiService::class.java)
    }

    val weatherService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApiService::class.java)
    }

    val wikiService: WikiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WikiApiService::class.java)
    }

    val searchService: GoogleSearchApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GoogleSearchApiService::class.java)
    }

    val geminiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}
