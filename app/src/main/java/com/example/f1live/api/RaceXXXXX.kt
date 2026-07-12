package com.example.f1live.api

data class RaceXXXXX(
    val Circuit: CircuitXXXXXX,
    val Laps: List<Lap>,
    val date: String,
    val raceName: String,
    val round: String,
    val season: String,
    val time: String,
    val url: String
)