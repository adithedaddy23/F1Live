package com.example.f1live.api

data class RaceTable(
    val Races: List<Race>,
    val round: String,
    val season: String
)