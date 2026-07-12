package com.example.f1live.repository

sealed class Routes(val routes: String) {
    data object F1GrandPrix : Routes("f1GrandPrix/{season}/{round}") {
        fun createRoute(season: String, round: String) = "f1GrandPrix/$season/$round"
    }
    data object Driver : Routes("driver/{season}/{driverId}") {
        fun createRoute2(season: String, driverId: String) = "driver/$season/$driverId"
    }
    data object F1GrandPrixArchieve : Routes("f1GrandPrixArchieve/{season}/{round}") {
        fun createRoute3(season: String, routes: String) = "f1GrandPrixArchieve/$season/$routes"
    }

    data object NewsScreen : Routes("news") {
        fun createRoute() = "news"
    }

//    data object PathAnimation: Routes("pathAnimation") {
//        fun createRoute4() = "pathAnimation"
//    }
    data object RaceScrubber : Routes("raceScrubber/{season}/{round}") {
        fun createRoute(season: String, round: String) = "raceScrubber/$season/$round"
    }
}
