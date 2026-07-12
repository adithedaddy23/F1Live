package com.example.f1live.repository

import android.util.Log
import com.example.f1live.api.ApiClient
import com.example.f1live.api.Circuits
import com.example.f1live.api.ConstructorStandings
import com.example.f1live.api.Constructors
import com.example.f1live.api.DriverDetails
import com.example.f1live.api.DriverStandings
import com.example.f1live.api.Drivers
import com.example.f1live.api.LapData
import com.example.f1live.api.Qualifying
import com.example.f1live.api.RaceList
import com.example.f1live.api.Result
import com.example.f1live.api.SprintX
import com.example.f1live.news.NewsArticle
import com.example.f1live.news.RssFeed

class F1Repository {
    private val api = ApiClient.api

    private val newsFeed = ApiClient.rssApi

    suspend fun getF1News(): List<NewsArticle>? {
        return try {
            Log.d("F1Repository", "Making RSS API call...")
            val response = ApiClient.rssApi.getF1News()
            Log.d("F1Repository", "RSS Response received: $response")

            val articles = response.channel?.articles
            Log.d("F1Repository", "Articles from channel: ${articles?.size ?: 0}")

            articles
        } catch (e: Exception) {
            Log.e("F1Repository", "Error in getF1News", e)
            throw e
        }
    }

    suspend fun getCircuits(year: String): Circuits {
        return api.getCircuits(year)
    }

    suspend fun getConstructors(year: String): Constructors {
        return api.getConstructors(year)
    }

    suspend fun getConstructorStandings(year: String): ConstructorStandings {
        return api.getConstructorsStanding(year)
    }

    suspend fun getDrivers(year: String): Drivers {
        return api.getDrivers(year)
    }

    suspend fun getDriverStandings(year: String): DriverStandings {
        return api.getDriversStandings(year)
    }

    suspend fun getQualifying(year: String, round: String): Qualifying {
        return api.getQualifying(year, round)
    }

    suspend fun getRaces(year: String): RaceList {
        return api.getRaces(year)
    }

    suspend fun getResults(year: String, round: String): Result {
        return api.getReults(year, round)
    }

    suspend fun getRacesByRound(year: String, round: String): RaceList {
        return api.getRacesByRound(year,round)
    }

    suspend fun getAllResults(year: String): Result {
        return api.getAllReults(year)
    }

    suspend fun getSprint(year: String, round: String): SprintX {
        return api.getSprint(year, round)
    }

    suspend fun getDriverDetails(year: String, driver_id: String): DriverDetails {
        return api.getDriverDetails(year,driver_id)
    }

    suspend fun getLapData(year: String, round: String) : LapData {
        return api.getLapData(year, round)
    }
}