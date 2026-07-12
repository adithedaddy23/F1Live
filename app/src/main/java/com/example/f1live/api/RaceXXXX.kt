package com.example.f1live.api

data class RaceXXXX(
    val Circuit: CircuitXXXXX,
    val Results: List<ResultXX>,
    val date: String,
    val raceName: String,
    val round: String,
    val season: String,
    val time: String,
    val url: String
)