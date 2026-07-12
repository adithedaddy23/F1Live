package com.example.f1live.api

data class StandingsListsX(
    val DriverStandings: List<DriverStanding>,
    val round: String,
    val season: String
)