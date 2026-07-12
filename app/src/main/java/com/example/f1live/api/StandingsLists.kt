package com.example.f1live.api

data class StandingsLists(
    val ConstructorStandings: List<ConstructorStanding>,
    val round: String,
    val season: String
)