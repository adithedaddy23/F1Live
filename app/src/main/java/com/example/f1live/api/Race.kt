package com.example.f1live.api

data class Race(
    val Circuit: CircuitX,
    val QualifyingResults: List<QualifyingResult>,
    val date: String,
    val raceName: String,
    val round: String,
    val season: String,
    val time: String,
    val url: String
)