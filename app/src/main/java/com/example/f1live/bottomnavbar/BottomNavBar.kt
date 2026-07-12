package com.example.f1live.bottomnavbar

import com.example.f1live.R

sealed class BottomNavScreen(
    val route: String,
    val label: String,
    val iconRes: Int // your painter resource id
) {
    object Race: BottomNavScreen("race", "Races", R.drawable.sports_score_24dp_e3e3e3_fill0_wght400_grad0_opsz24)
    object Standings : BottomNavScreen("standings", "Standings", R.drawable.emoji_events_24dp_000000)
//    object Drivers : BottomNavScreen("drivers", "Drivers", R.drawable.sports_motorsports_24dp_000000)
//    //    object Rankings : BottomNavScreen("rankings", "Rankings", R.drawable.ranking)
//    object Constructors : BottomNavScreen("constructors", "Constructors", R.drawable.multiple_users_silhouette)
    object Archive: BottomNavScreen("archive", "Archive", R.drawable.archive_24dp_000000, )

    object News: BottomNavScreen("news", "News", R.drawable.news_24dp_000000_fill0_wght400_grad0_opsz24)
}

val bottomNavItems = listOf(
    BottomNavScreen.Race,
    BottomNavScreen.Standings,
//    BottomNavScreen.Drivers,
////    BottomNavScreen.Rankings,
//    BottomNavScreen.Constructors,
    BottomNavScreen.Archive,
    BottomNavScreen.News
)
