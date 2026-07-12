package com.example.f1live.api

data class RaceXX(
    val Circuit: CircuitXXX,
    val Results: List<ResultX>,
    val date: String,
    val raceName: String,
    val round: String,
    val season: String,
    val time: String,
    val url: String
)