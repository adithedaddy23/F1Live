package com.example.f1live.api

data class RaceXXX(
    val Circuit: CircuitXXXX,
    val SprintResults: List<SprintResult>,
    val date: String,
    val raceName: String,
    val round: String,
    val season: String,
    val time: String,
    val url: String
)