package com.example.f1live.api

data class MRData(
    val CircuitTable: CircuitTable,
    val limit: String,
    val offset: String,
    val series: String,
    val total: String,
    val url: String,
    val xmlns: String
)