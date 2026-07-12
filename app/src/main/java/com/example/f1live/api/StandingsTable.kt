package com.example.f1live.api

data class StandingsTable(
    val StandingsLists: List<StandingsLists>,
    val round: String,
    val season: String
)