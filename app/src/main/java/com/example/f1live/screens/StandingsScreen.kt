package com.example.f1live.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.f1live.R
import com.example.f1live.api.ConstructorStanding
import com.example.f1live.api.ConstructorStandings
import com.example.f1live.api.Driver
import com.example.f1live.api.DriverStanding
import com.example.f1live.api.DriverStandings
import com.example.f1live.api.Drivers
import com.example.f1live.repository.DriversImg
import com.example.f1live.api.UiState
import com.example.f1live.repository.Car
import com.example.f1live.repository.Routes
import com.example.f1live.repository.logo
import com.example.f1live.viewmodel.F1ViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun StandingsScreen(
    viewModel: F1ViewModel = viewModel(),
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    ) {
    val tabs = listOf("Drivers", "Constructors")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    val driverStandingsState by viewModel.driverStandingsState.collectAsState()
    val constructorStandingsState by viewModel.constructorStandingsState.collectAsState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomNavPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LaunchedEffect(Unit) {
        viewModel.fetchDriverStandings()
        viewModel.fetchConstructorStandings()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarPadding),

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column {
                Text(
                    text = "Standings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    letterSpacing = 2.sp
                )

                // Custom Tab Row with animated indicator based on scroll position
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        // Animated indicator that moves smoothly during swipe
                        val currentTab = tabPositions[pagerState.currentPage]
                        val targetTab = tabPositions.getOrNull(pagerState.targetPage) ?: currentTab
                        val fraction = pagerState.currentPageOffsetFraction

                        val indicatorLeft = lerp(currentTab.left, targetTab.left, fraction)
                        val indicatorRight = lerp(currentTab.right, targetTab.right, fraction)

                        Box(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.BottomStart)
                                .offset(x = indicatorLeft)
                                .width(indicatorRight - indicatorLeft)
                                .height(3.dp)
                                .background(Color(0xFFE10600))
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title.uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                    letterSpacing = 1.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> DriverStandingsContent(driverStandingsState, navController,sharedTransitionScope,
                    animatedContentScope)
                1 -> ConstructorStandingsContent(constructorStandingsState)
            }
        }
    }
}

// Helper function for smooth indicator animation
private fun lerp(start: Dp, stop: Dp, fraction: Float): Dp {
    return start + (stop - start) * fraction
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun DriverStandingsContent(
    state: UiState<DriverStandings>,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    ) {
    val bottomNavPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    when (state) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator(
                    color = Color(0xFF2196F3)
                )
            }
        }
        is UiState.Success -> {
            val standings = state.data.MRData.StandingsTable.StandingsLists.firstOrNull()
                ?.DriverStandings ?: emptyList()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 16.dp + bottomNavPadding + 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(standings) { standing ->
                    DriverStandingCard(
                        standing,
                        navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope
                    )
                }
            }
        }
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Unable to load standings",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Please try again",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
fun getTeamLogo(constructorName: String): String {
    val nameToFind = when (constructorName) {
        "Racing Bulls" -> "Racing Bull"
        "Cadillac F1 Team" -> "Cadillac"
        "Audi" -> "Audi"  // API returns "Audi", logo object also "Audi" — fine
        else -> constructorName
    }
    return logo.teamLogos.find { it.name == nameToFind }?.logoUrl ?: ""
}

@OptIn(ExperimentalSharedTransitionApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DriverStandingCard(
    standing: DriverStanding,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    ) {
    // Get all the data needed for the card
    val constructor = standing.Constructors.firstOrNull()
    val constructorId = constructor?.constructorId ?: ""
    val constructorName = constructor?.name ?: ""
    val driverId = standing.Driver.driverId
    val year = LocalDate.now().year

    val teamColorInfo = getTeamColorInfo(constructorId)
    val driverImage = getDriverImage(standing.Driver.driverId)
    val teamLogo = getTeamLogo(constructorName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
            .clickable{
                navController.navigate(Routes.Driver.createRoute2(year.toString(),driverId))
            }, // <-- ⭐ YOUR FIX IS HERE
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize() // This will now correctly fill the 100dp or larger height
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1E1E1E),
                            Color(0xFF2A2A2A).copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Position
                Box(
                    modifier = Modifier.width(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = standing.positionText ?: standing.position ?: "-",

                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 2. Driver Image
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(teamColorInfo.darkVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(driverImage)
                            .crossfade(300)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "${standing.Driver.givenName} ${standing.Driver.familyName}",
                        modifier = Modifier
                            .height(90.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 3. Driver and Team Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Driver Name
                    with(sharedTransitionScope) {
                        Text(
                            text = standing.Driver.givenName ?: "Unknown",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "race-name-${standing.Driver.givenName}"),
                                animatedVisibilityScope = animatedContentScope
                            )
                        )
                    }
                    with(sharedTransitionScope) {
                        Text(
                            text = (standing.Driver.familyName ?: "").uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "race-name-${standing.Driver.familyName}"),
                                animatedVisibilityScope = animatedContentScope
                            )
                        )
                    }


                    Spacer(modifier = Modifier.height(4.dp))

                    // Team Logo and Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(teamLogo)
                                .crossfade(300)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .build(),
                            contentDescription = "$constructorName Logo",
                            modifier = Modifier
                                .height(16.dp)
                                .padding(end = 6.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = constructorName ?: "Unknown",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 4. Points
                Box(
                    modifier = Modifier.width(65.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = standing.points ?: "NA",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "PTS",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier.width(25.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.chevron_forward_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = "More",
                        modifier = Modifier.size(48.dp)
                    )

                }
            }
        }
    }
}
@Composable
fun ConstructorStandingsContent(state: UiState<ConstructorStandings>) {
    val bottomNavPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    when (state) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = Color(0xFFE10600),
                    strokeWidth = 3.dp
                )
            }
        }
        is UiState.Success -> {
            val standings = state.data.MRData.StandingsTable.StandingsLists.firstOrNull()
                ?.ConstructorStandings ?: emptyList()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 16.dp + bottomNavPadding + 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(standings) { standing ->
                    ConstructorStandingCard(standing)
                }
            }
        }
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Unable to load standings",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Please try again",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ConstructorStandingCard(standing: ConstructorStanding) {
    val teamColorInfo = getTeamColorInfo(standing.Constructor.constructorId)
    val carImage = getCarImage(standing.Constructor.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(teamColorInfo.backgroundBrush)
        ) {
            // Subtle pattern overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            center = Offset(500f, 0f),
                            radius = 800f
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Position and Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    // Position circle
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Color.White.copy(alpha = 0.15f),
                                CircleShape
                            )
                            .border(
                                2.dp,
                                Color.White.copy(alpha = 0.4f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = standing.positionText ?: standing.position ?: "-",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Team name
                    Text(
                        text = (standing.Constructor.name ?: "Unknown Team").uppercase(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.2.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Points with better visibility
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.25f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = standing.points ?: "0",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }

                // Right side - Car Image
                Box(
                    modifier = Modifier.weight(1.1f),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(carImage)
                            .crossfade(300)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "${standing.Constructor.name} car",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

data class TeamColorInfo(
    val primaryColor: Color,
    val darkVariant: Color,
    val backgroundBrush: Brush
)

fun getTeamColorInfo(constructorId: String): TeamColorInfo {
    return when (constructorId.lowercase()) {
        "mclaren" -> {
            val primary = Color(0xFFFF8000)
            val dark = Color(0xFFB35900)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF8B3A00)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        "mercedes" -> {
            val primary = Color(0xFF00D2BE)
            val dark = Color(0xFF009C8B)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF006B5F)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        "ferrari" -> {
            val primary = Color(0xFFDC0000)
            val dark = Color(0xFF9E0000)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF6B0000)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        "red_bull" -> {
            val primary = Color(0xFF0600EF)
            val dark = Color(0xFF0400A3)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF020070)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        "williams" -> {
            val primary = Color(0xFF005AFF)
            val dark = Color(0xFF003DAB)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF002775)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        "rb", "racing_bulls" -> {
            val primary = Color(0xFF6692FF)
            val dark = Color(0xFF4C6BBF)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF354B8A)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        "aston_martin" -> {
            val primary = Color(0xFF006F62)
            val dark = Color(0xFF004D44)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF003330)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        "haas" -> {
            val primary = Color(0xFFEEEEEE)
            val dark = Color(0xFFAAAAAA)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF777777)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        "alpine" -> {
            val primary = Color(0xFFFF87BC)
            val dark = Color(0xFFB35F82)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF7A3E57)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        // Kick Sauber rebranded to Audi for 2026
        "audi" -> {
            val primary = Color(0xFFBB0000) // Audi's signature red
            val dark = Color(0xFF880000)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF550000)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        // Cadillac/TWG (new 11th team for 2026)
        "cadillac", "twg" -> {
            val primary = Color(0xFFB8985A) // Cadillac gold/champagne
            val dark = Color(0xFF8A6E3A)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF5C4720)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
        else -> {
            val primary = Color(0xFF555555)
            val dark = Color(0xFF333333)
            TeamColorInfo(
                primary, dark,
                Brush.linearGradient(
                    colors = listOf(primary, dark, Color(0xFF1A1A1A)),
                    start = Offset(0f, 0f),
                    end = Offset(1500f, 800f)
                )
            )
        }
    }
}

fun getDriverImage(driverId: String): String {
    val driverMap = mapOf(
        "piastri" to "Oscar Piastri",
        "norris" to "Lando Norris",
        "russell" to "George Russell",
        "antonelli" to "Andrea Kimi Antonelli",
        "leclerc" to "Charles Leclerc",
        "hamilton" to "Lewis Hamilton",
        "max_verstappen" to "Max Verstappen",
        "tsunoda" to "Yuki Tsunoda",
        "albon" to "Alexander Albon",
        "hadjar" to "Isack Hadjar",
        "stroll" to "Lance Stroll",
        "alonso" to "Fernando Alonso",
        "hulkenberg" to "Nico Hülkenberg",
        "bortoleto" to "Gabriel Bortoleto",
        "ocon" to "Esteban Ocon",
        "bearman" to "Oliver Bearman",
        "gasly" to "Pierre Gasly",
        "colapinto" to "Franco Colapinto",
        "sainz" to "Carlos Sainz",
        "lawson" to "Liam Lawson",
        "doohan" to "Jack Doohan",
        "bottas" to "Valtteri Bottas",
        "arvid_lindblad" to "Arvid Lindblad",
        "perez" to "Sergio Perez"
    )

    val driverName = driverMap[driverId] ?: return ""
    return DriversImg.drivers.find { it.name == driverName }?.imgUrl ?: ""
}

fun getCarImage(constructorName: String): String {
    val nameToFind = when (constructorName) {
        "Audi" -> "Audi F1 Team"
        "Cadillac F1 Team" -> "Cadillac F1 Team"  // already matches
        else -> constructorName
    }
    return Car.cars.find { it.name == nameToFind }?.imgUrl ?: ""
}