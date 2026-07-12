package com.example.f1live.news

import retrofit2.http.GET

interface F1NewsApi {
    @GET("rss/f1/news/")
    suspend fun getF1News(): RssFeed
}