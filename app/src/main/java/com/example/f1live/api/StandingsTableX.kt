package com.example.f1live.api

data class StandingsTableX(
    val StandingsLists: List<StandingsListsX>,
    val round: String,
    val season: String
)