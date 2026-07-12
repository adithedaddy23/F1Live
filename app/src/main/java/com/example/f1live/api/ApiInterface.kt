package com.example.f1live.api

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {
 @GET("/ergast/f1/{year}/circuits/")
 suspend fun getCircuits(@Path("year") year: String): Circuits

 @GET("/ergast/f1/{year}/constructors/")
 suspend fun getConstructors(@Path("year") year: String): Constructors

 @GET("/ergast/f1/{year}/constructorstandings/")
 suspend fun getConstructorsStanding(@Path("year") year: String): ConstructorStandings

 @GET("/ergast/f1/{year}/drivers/")
 suspend fun getDrivers(@Path("year")year: String): Drivers

 @GET("/ergast/f1/{year}/driverstandings/")
 suspend fun getDriversStandings(@Path("year")year: String): DriverStandings

 @GET("/ergast/f1/{year}/{round}/qualifying/")
 suspend fun getQualifying(@Path("year")year: String, @Path("round")round: String): Qualifying

 @GET("/ergast/f1/{year}/races/")
 suspend fun getRaces(@Path("year")year: String): RaceList

 @GET("/ergast/f1/{year}/{round}/races/")
 suspend fun getRacesByRound(@Path("year")year: String, @Path("round")round: String): RaceList

 @GET("/ergast/f1/{year}/{round}/results/")
 suspend fun getReults(@Path("year")year: String, @Path("round")round: String): Result

 @GET("/ergast/f1/{year}/results/")
 suspend fun getAllReults(@Path("year")year: String): Result

 @GET("/ergast/f1/{year}/{round}/sprint/")
 suspend fun getSprint(@Path("year")year: String, @Path("round")round: String): SprintX

 @GET("/ergast/f1/{year}/drivers/{driver_id}/results/")
 suspend fun getDriverDetails(@Path("year")year: String, @Path("driver_id")driver_id: String): DriverDetails

    @GET("/ergast/f1/{year}/{round}/laps/")
    suspend fun getLapData(
        @Path("year") year: String,
        @Path("round") round: String
    ): LapData
}