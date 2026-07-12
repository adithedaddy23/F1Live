package com.example.f1live.api

data class RaceX(
    val Circuit: CircuitXX,
    val FirstPractice: FirstPractice,
    val Qualifying: QualifyingX,
    val SecondPractice: SecondPractice,
    val Sprint: Sprint,
    val SprintQualifying: SprintQualifying,
    val ThirdPractice: ThirdPractice,
    val date: String,
    val raceName: String,
    val round: String,
    val season: String,
    val time: String,
    val url: String
)