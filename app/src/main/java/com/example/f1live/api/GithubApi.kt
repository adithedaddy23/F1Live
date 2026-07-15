package com.example.f1live.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApi {
    @GET("repos/adithedaddy23/F1Live/releases/latest")
    suspend fun getLatestRelease(): GitHubRelease

    @GET("repos/adithedaddy23/F1Live/releases/tags/{tag}")
    suspend fun getReleaseByTag(@Path("tag") tag: String): GitHubRelease
    companion object {
        fun create(): GitHubApi = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }
}

data class GitHubRelease(
    val tag_name: String,        // e.g. "v1.0.0"
    val html_url: String,        // link to release page
    val body: String?,           // release notes / changelog
    val assets: List<ReleaseAsset>
)

data class ReleaseAsset(
    val name: String,
    val browser_download_url: String
)