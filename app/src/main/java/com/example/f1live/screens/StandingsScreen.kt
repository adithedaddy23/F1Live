package com.example.f1live.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import com.example.f1live.utils.ripple
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
    val year = LocalDate.now().year
    val driverState by viewModel.driversState.collectAsState()
    val driverStandingsState by viewModel.driverStandingsState.collectAsState()
    val constructorStandingsState by viewModel.constructorStandingsState.collectAsState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomNavPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LaunchedEffect(Unit) {
        viewModel.fetchDriverStandings()
        viewModel.fetchConstructorStandings()
        viewModel.fetchDrivers(year.toString())
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
    val constructor = standing.Constructors.firstOrNull()
    val constructorId = constructor?.constructorId ?: ""
    val constructorName = constructor?.name ?: ""
    val driverId = standing.Driver.driverId
    val year = LocalDate.now().year

    val teamColorInfo = getTeamColorInfo(constructorId)
    val driverImage = getDriverImage(standing.Driver.driverId)
    val teamLogo = getTeamLogo(constructorName)

    val position = standing.positionText ?: standing.position ?: "-"
    val ordinalSuffix = remember(position) { ordinalSuffixFor(position) }


    // Paint reused across recompositions/draws; dithering is what actually kills the
    // gradient banding — Modifier.background(brush) does not enable it, but a raw
    // Paint drawn via drawIntoCanvas does.
    val gradientPaint = remember {
        Paint().apply { asFrameworkPaint().isDither = true }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable{
                navController.navigate(Routes.Driver.createRoute2(year.toString(), driverId))
            },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    // Primary team color on the left fading into dark on the right,
                    // with a soft midpoint stop so the hue shift itself is gentler —
                    // fewer big jumps in value/saturation means less visible banding
                    // even before dithering is factored in.
                    val dark = Color(0xFF141414)
                    val mid = lerp(teamColorInfo.primaryColor, dark, 0.5f)
                    gradientPaint.shader = LinearGradientShader(
                        from = Offset(0f, 0f),
                        to = Offset(size.width, 0f),
                        colors = listOf(teamColorInfo.primaryColor, mid, dark),
                        colorStops = listOf(0f, 0.6f, 1f)
                    )
                    onDrawBehind {
                        drawIntoCanvas { canvas ->
                            canvas.drawRect(0f, 0f, size.width, size.height, gradientPaint)
                        }
                    }
                }
        ) {
            // Big faint position number watermark, bottom-right
            Text(
                text = position,
                fontSize = 90.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 10.dp, y = 0.dp)
            )

            // Driver image, right-aligned, bleeding off the bottom edge
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(driverImage)
                    .crossfade(300)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "${standing.Driver.givenName} ${standing.Driver.familyName}",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(110.dp)
                    .fillMaxHeight()
                    .padding(top = 12.dp)
            )

            // Left-to-right fade so text stays readable over the photo edge
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = 500f
                        )
                    )
            )

            // Foreground content
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp, end = 130.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                // Team badge row: logo chip + driver name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(teamLogo)
                                .crossfade(300)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .build(),
                            contentDescription = "$constructorName Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(16.dp)

                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    with(sharedTransitionScope) {
                        Text(
                            text = "${standing.Driver.givenName ?: ""} ${(standing.Driver.familyName ?: "").let { if (it.isNotBlank()) it.uppercase() else "" }}".trim(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "race-name-${standing.Driver.familyName}"),
                                animatedVisibilityScope = animatedContentScope
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // "Nth place"
                Text(
                    text = "$position$ordinalSuffix place",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Points
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)) {
                            append(standing.points ?: "0")
                        }
                        withStyle(SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.85f))) {
                            append("pts")
                        }
                    }
                )
            }

            // Tap affordance: a small chevron chip in a translucent circle, top-right.
            // Signals "this leads somewhere" without competing with the points/position text.
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.30f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.chevron_forward_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = "View driver details",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/** Returns "st", "nd", "rd", or "th" for a numeric position string like "5". */
private fun ordinalSuffixFor(position: String): String {
    val n = position.toIntOrNull() ?: return ""
    return when {
        n % 100 in 11..13 -> "th"
        n % 10 == 1 -> "st"
        n % 10 == 2 -> "nd"
        n % 10 == 3 -> "rd"
        else -> "th"
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
    val teamLogo = getTeamLogo(standing.Constructor.name ?: "")

    val position = standing.positionText ?: standing.position ?: "-"
    val ordinalSuffix = remember(position) { ordinalSuffixFor(position) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0D))
        ) {
            // Diagonal team-color wash bleeding in from the right, behind the car.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                teamColorInfo.primaryColor.copy(alpha = 0.35f),
                                teamColorInfo.primaryColor.copy(alpha = 0.55f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1100f, 300f)
                        )
                    )
                    .zIndex(0f)
            )

            // Racing stripe accents — a handful of angled bars, clipped to the card.
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
            ) {
                val stripeColor = Color.White.copy(alpha = 0.05f)
                val stripeWidth = 26.dp.toPx()
                val gap = 22.dp.toPx()
                var x = size.width * 0.45f
//                repeat(6) {
//                    rotate(degrees = 20f, pivot = Offset(x, size.height / 2f)) {
//                        drawRect(
//                            color = stripeColor,
//                            topLeft = Offset(x, -40f),
//                            size = androidx.compose.ui.geometry.Size(stripeWidth, size.height + 80f)
//                        )
//                    }
//                    x += stripeWidth + gap
//                }
            }

            // Car image — now a full-bleed BACKGROUND layer (zIndex 2), independent of the
            // text column's width. This is what was squeezing the constructor name before:
            // it used to share a weighted Row cell with the text, capping available width.
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(carImage)
                    .crossfade(300)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "${standing.Constructor.name} car",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(0.70f)
                    .fillMaxHeight(0.60f)
                    .offset(x = 60.dp)
                    .graphicsLayer(scaleX = -1f)
                    .zIndex(2f)
            )

            // Scrim so the car's left edge never fights the text for legibility, regardless
            // of how wide/dark a given team's car render is.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Black.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = 650f
                        )
                    )
                    .zIndex(3f)
            )

            // Foreground content — highest zIndex, always drawn on top of the car.
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(20.dp)
                    .fillMaxWidth(0.72f)
                    .zIndex(4f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo + "Team" / name badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(teamLogo)
                                .crossfade(300)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .build(),
                            contentDescription = "${standing.Constructor.name} logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(22.dp)

                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .width(1.5.dp)
                            .height(28.dp)
                            .background(teamColorInfo.primaryColor.copy(alpha = 0.8f))
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = "Team",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        // Full name, no truncation: up to 2 lines, tight line height, no ellipsis.
                        // Now that this column isn't fighting the car for width, one line is
                        // enough for almost every constructor name; two lines is just a safety net.
                        Text(
                            text = standing.Constructor.name ?: "Unknown",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 2,
                            softWrap = true,
                            lineHeight = 20.sp
                        )
                    }
                }

                Column {
                    // "1st place"
                    Text(
                        text = "$position$ordinalSuffix place",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Points
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 30.sp, fontWeight = FontWeight.Black, color = Color.White)) {
                                append(standing.points ?: "0")
                            }
                            withStyle(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.85f))) {
                                append("pts")
                            }
                        }
                    )
                }
            }
        }
    }
}

// NOTE: ordinalSuffixFor(position: String) is defined in DriverStandingCard.kt.
// Make sure it's not declared `private` there (private top-level functions are
// file-private in Kotlin) — change it to internal/public, or move it to a shared Utils.kt.

// NOTE: ordinalSuffixFor(position: String) is already defined in DriverStandingCard.kt
// (returns "st"/"nd"/"rd"/"th"). Don't redeclare it here if both files share a package —
// just make sure it isn't private to that file, or move it somewhere shared.

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