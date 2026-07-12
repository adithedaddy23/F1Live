package com.example.f1live.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.f1live.news.F1NewsApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object ApiClient {
    private const val BASE_URL = "https://api.jolpi.ca/"
    private const val F1_RSS_BASE_URL = "https://www.autosport.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }

    private val rssRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(F1_RSS_BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                            .build()
                        Log.d("ApiClient", "RSS Request: ${request.url}")
                        val response = chain.proceed(request)
                        Log.d("ApiClient", "RSS Response code: ${response.code}")
                        response
                    }
                    .build()
            )
            .build()
    }

    val rssApi: F1NewsApi by lazy {
        rssRetrofit.create(F1NewsApi::class.java)
    }

}

// Helper function to strip HTML from description
fun stripHtml(html: String): String {
    return html
        .replace(Regex("<[^>]*>"), "") // Remove HTML tags
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .trim()
}

// Helper function to format pub date
@RequiresApi(Build.VERSION_CODES.O)
fun formatPubDate(pubDate: String): String {
    return try {
        // BBC uses RFC 822 format: "Mon, 05 Jan 2026 11:20:24 GMT"
        val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
        val dateTime = ZonedDateTime.parse(pubDate, formatter)

        // Format to more readable format
        val now = ZonedDateTime.now()
        val duration = Duration.between(dateTime, now)

        when {
            duration.toDays() == 0L -> {
                when {
                    duration.toHours() == 0L -> "${duration.toMinutes()}m ago"
                    else -> "${duration.toHours()}h ago"
                }
            }
            duration.toDays() < 7 -> "${duration.toDays()}d ago"
            else -> dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        }
    } catch (e: Exception) {
        Log.e("formatPubDate", "Error parsing date: $pubDate", e)
        pubDate
    }
}