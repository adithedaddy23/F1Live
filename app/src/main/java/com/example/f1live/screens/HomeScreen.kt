package com.example.f1live.screens

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.RoundedPolygon
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import com.example.f1live.R
import com.example.f1live.api.RaceX
import com.example.f1live.api.Result
import com.example.f1live.api.ResultX
import com.example.f1live.api.UiState
import com.example.f1live.repository.DriversImg
import com.example.f1live.repository.Routes
import com.example.f1live.repository.TrackPhotos
import com.example.f1live.viewmodel.F1ViewModel
import com.kyant.capsule.ContinuousCapsule
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Homescreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: F1ViewModel = viewModel(),
    navController: NavController
) {
//    val raceResultsMap by viewModel.raceResultsMap.collectAsState()
    val year = LocalDate.now().year
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomNavPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // Track if this is the initial load
    var isInitialLoad by remember { mutableStateOf(true) }

    // Track manual refresh state separately
    var isManualRefreshing by remember { mutableStateOf(false) }

    // Show refresh indicator only during manual refresh
    val isRefreshing = isManualRefreshing

    // Define the onRefresh action
    val onRefresh = {
        isInitialLoad = false
        isManualRefreshing = true
        viewModel.fetchAllDataForYear(year.toString(), forceRefresh = true)
    }

    val raceState by viewModel.racesState.collectAsState()
    // ...

    // Compute once per raceState change, in valid composable scope
    val categorizedRaces = remember(raceState) {
        (raceState as? UiState.Success)?.let { categorizeRaces(it.data.MRData.RaceTable.Races) }
    }

    LaunchedEffect(year) {
        viewModel.fetchAllDataForYear(year.toString())
    }

    // Mark initial load as complete and stop manual refresh indicator
    LaunchedEffect(raceState) {
        if (raceState is UiState.Success) {
            if (isInitialLoad) {
                isInitialLoad = false
            }
            if (isManualRefreshing) {
                isManualRefreshing = false
            }
        }
        if (raceState is UiState.Error) {
            if (isManualRefreshing) {
                isManualRefreshing = false
            }
        }
    }



    // Fetch results for past races when races are loaded
    LaunchedEffect(raceState) {
        if (raceState is UiState.Success) {
            val races = (raceState as UiState.Success).data.MRData.RaceTable.Races
            val categorized = categorizeRaces(races)
            val pastRaceRounds = categorized.past.map { it.round }
            if (pastRaceRounds.isNotEmpty()) {
                viewModel.fetchResultsForPastRaces(year.toString(), pastRaceRounds)
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),



    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = 16.dp,
                end = 16.dp,
                top = 24.dp + statusBarPadding, // <-- Apply it here
                bottom = 24.dp + bottomNavPadding + 80.dp)
        ) {
            item {
                Text(
                    text = "F1 Season $year",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            when (val state = raceState) {
                is UiState.Loading -> {
                    // Show loading indicator only on initial load
                    if (isInitialLoad) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingIndicator(
                                    color = Color(0xFF2196F3),
                                )
                            }
                        }
                    }
                }
                is UiState.Success -> {

                    // Current Weekend Race (if exists)
                    categorizedRaces?.currentWeekend?.let { currentRace ->
                        item {
                            Text(
                                text = "This Weekend",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        item {
                            RaceCard(
                                race = currentRace,
                                isCurrentWeekend = true,
                                isPast = false,
                                navController = navController,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedContentScope = animatedContentScope,
                                viewModel = viewModel
                            )
                        }
                    }

                    // Upcoming Races
                    if (categorizedRaces?.upcoming?.isNotEmpty() == true) {
                        item {
                            Text(
                                text = "Upcoming Races",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(categorizedRaces.upcoming, key = { it.round }) { race ->
                            RaceCard(
                                race = race,
                                isCurrentWeekend = false,
                                isPast = false,
                                showDate = true,
                                navController = navController,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedContentScope = animatedContentScope,
                                viewModel = viewModel
                            )
                        }
                    }

                    // Past Races
                    if (categorizedRaces?.past?.isNotEmpty() == true) {
                        item {
                            Text(
                                text = "Past Races",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3).copy(alpha = 0.9f),
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(categorizedRaces.past, key = { it.round }) { race ->
                            RaceCard(
                                race = race,
                                isCurrentWeekend = false,
                                isPast = true,
                                navController = navController,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedContentScope = animatedContentScope,
                                viewModel = viewModel
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "Error loading races",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

data class CategorizedRaces(
    val currentWeekend: RaceX? = null,  // <-- Changed
    val upcoming: List<RaceX> = emptyList(), // <-- Changed
    val past: List<RaceX> = emptyList()     // <-- Changed
)

@RequiresApi(Build.VERSION_CODES.O)
fun categorizeRaces(races: List<RaceX>): CategorizedRaces {
    // Get the current date in the user's local time zone
    val today = LocalDate.now(ZoneId.systemDefault())

    var currentWeekend: RaceX? = null
    val upcoming = mutableListOf<RaceX>()
    val past = mutableListOf<RaceX>()

    // --- NEW LOGIC ---
    // First, find the "current" race.
    // We define "current" as any day from Friday before the race
    // to the Monday after the race (to see results).
    for (race in races) {
        val raceDate = LocalDate.parse(race.date) // This is the Sunday
        val raceWeekendStart = raceDate.minusDays(2) // Friday
        val raceWeekendEnd = raceDate.plusDays(1)   // Monday

        // Check if today is within that Friday-Monday window
        if (today in raceWeekendStart..raceWeekendEnd) {
            currentWeekend = race
            break // Found it, stop looking
        }
    }

    // Now, categorize all other races
    races.forEach { race ->
        // Skip the race we just set as the current weekend
        if (race.raceName == currentWeekend?.raceName) {
            return@forEach
        }

        val raceDate = LocalDate.parse(race.date)
        if (raceDate.isAfter(today)) {
            upcoming.add(race)
        } else {
            past.add(race)
        }
    }

    return CategorizedRaces(
        currentWeekend = currentWeekend,
        // Sort the lists so they are in chronological order
        upcoming = upcoming.sortedBy { LocalDate.parse(it.date) },
        past = past.sortedByDescending { LocalDate.parse(it.date) }
    )
}

private val darkOverlayBrush = Brush.verticalGradient(
    colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.6f))
)
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RaceCard(
    race: RaceX,
    isCurrentWeekend: Boolean = false,
    isPast: Boolean = false,
    showDate: Boolean = false,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: F1ViewModel
) {

    val results by remember(race.round) {
        viewModel.raceResultsMap
            .map { it[race.round] ?: emptyList() }
            .distinctUntilChanged()
    }.collectAsState(initial = emptyList())


    // Move expensive computations outside recomposition scope
//    val trackImageResId = remember(race.raceName, race.Circuit.circuitName) {
//        TrackPhotos.getTrackList().firstOrNull { track ->
//            race.raceName.contains(track.gpName, ignoreCase = true) ||
//                    race.Circuit.circuitName.contains(track.circuitName, ignoreCase = true)
//        }?.imgUrl
//    }
//    val hazeState = rememberHazeState()

    val cardShape = MaterialTheme.shapes.medium

    val containerColor = remember(isCurrentWeekend) {
        if (isCurrentWeekend) Color(0xFF2196F3) else Color.Transparent
    }

    val isTransitionActive = animatedContentScope.transition.isRunning

    val context = LocalContext.current

    val trackImageResId = remember(race.raceName, race.Circuit.circuitName) {
        TrackPhotos.getTrackList().firstOrNull { track ->
            race.raceName.contains(track.gpName, ignoreCase = true) ||
                    race.Circuit.circuitName.contains(track.circuitName, ignoreCase = true)
        }?.imgUrl
    }

    // Memoized — only rebuilt when trackImageResId actually changes
    val imageRequest = remember(trackImageResId) {
        ImageRequest.Builder(context)
            .data(trackImageResId ?: R.drawable._025_japanese_gp___race_start_2)
            .crossfade(150) // shortened from 300 — cheaper on fast scroll
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .placeholder(ColorDrawable(android.graphics.Color.DKGRAY)) // avoids layout pop
            .size(600, 300)
            .precision(Precision.INEXACT)
            .build()
    }
    val raceState by viewModel.racesState.collectAsState()

    val categorizedRaces = remember(raceState) {
        (raceState as? UiState.Success)?.let { categorizeRaces(it.data.MRData.RaceTable.Races) }
    }

    LaunchedEffect(categorizedRaces) {
        categorizedRaces?.past?.take(10)?.forEach { race ->
            val imgRes = TrackPhotos.getTrackList().firstOrNull { track ->
                race.raceName.contains(track.gpName, ignoreCase = true) ||
                        race.Circuit.circuitName.contains(track.circuitName, ignoreCase = true)
            }?.imgUrl

            val request = ImageRequest.Builder(context)
                .data(imgRes ?: R.drawable._025_japanese_gp___race_start_2)
                .size(800, 400)
                .build()

            context.imageLoader.enqueue(request)
        }
    }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    Routes.F1GrandPrix.createRoute(race.season, race.round)
                )
            },
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = if (isCurrentWeekend) 8.dp else 0.dp
//        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isPast) 240.dp else 200.dp)
        ) {
            // Background Image (for non-current weekend cards)
            if (!isCurrentWeekend) {



                AsyncImage(
                    model = imageRequest,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),           // removed hazeSource
                    contentScale = ContentScale.Crop,
                    alpha = 0.9f,
                    filterQuality = FilterQuality.Low ,
                    placeholder = ColorPainter(Color.DarkGray)// Low is faster than None for downscaled images
                )
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top
                ) {

//                    Button(
//                        onClick = {},
//                        modifier = Modifier.padding(8.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.primary.copy(0.8f),
//                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                        ),
//                        shape = CircleShape,
//                    ) {
//                        Text(
//                            text = "Round ${race.round}",
//                            fontSize = 12.sp
//                        )
//                    }


//                    IconButton(
//                        onClick = {
//                            navController.navigate(
//                                Routes.F1GrandPrix.createRoute(race.season, race.round)
//                            )
//                        },
//                        modifier = Modifier
//                            .padding(8.dp)
//                            .clip(CircleShape)
////                            .background(color = Color.Black, shape = CircleShape)
//                            .hazeEffect(
//                                state = hazeState,
//                                style = CupertinoMaterials.ultraThin()
//                            ) {
//                                blurRadius = 10.dp
//                                tints = listOf(
//                                    HazeTint(Color.White.copy(0.5f))
//                                )
//                            },
////                        colors = IconButtonDefaults.iconButtonColors(
////                            containerColor =Color(0xFF2196F3).copy(0.7f),
////                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
////                        )
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.arrow_outward_24dp_000000_fill0_wght400_grad0_opsz24),
//                            contentDescription = "Read article",
//                            modifier = Modifier.size(20.dp),
//                            tint = Color.Black
//                        )
//                    }

                }

                // Dark overlay for better text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            darkOverlayBrush
                        )
                )
            }

            // Content Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                // Top Section: Race Name, Location, and Round Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            with(sharedTransitionScope) {
                                Text(
                                    text = race.raceName,
                                    style = MaterialTheme.typography.titleLargeEmphasized,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = if (isTransitionActive) {
//                                        Modifier.sharedElement(
//                                            sharedTransitionScope.rememberSharedContentState(key = "race-name-${race.round}"),
//                                            animatedVisibilityScope = animatedContentScope
//                                        )
                                        Modifier
                                            .sharedBounds(
                                            rememberSharedContentState(key = "race-name-${race.round}"),
                                            animatedVisibilityScope = animatedContentScope,
                                            enter = fadeIn(),
                                            exit = fadeOut(),
                                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                        )
//                                            .renderInSharedTransitionScopeOverlay()
                                    } else Modifier
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.End,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    text = "More",
//                                    fontSize = 12.sp,
//                                )
//                                Spacer(Modifier.width(2.dp))
//                                Icon(
//                                    painter = painterResource(R.drawable.arrow_outward_24dp_000000_fill0_wght400_grad0_opsz24),
//                                    contentDescription = "More Info",
//                                    modifier = Modifier.size(13.dp)
//                                )
//                            }

                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        with(sharedTransitionScope) {
                            Text(
                                text = race.Circuit.circuitName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.sharedBounds(
                                    sharedTransitionScope.rememberSharedContentState(key = "circuit-name-${race.round}"),
                                    animatedVisibilityScope = animatedContentScope,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                )
                            )
                        }
                        with(sharedTransitionScope) {
                            Text(
                                text = "${race.Circuit.Location.locality}, ${race.Circuit.Location.country}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.sharedBounds(
                                    sharedTransitionScope.rememberSharedContentState(key = "location-${race.round}"),
                                    animatedVisibilityScope = animatedContentScope,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                )
                            )
                        }

                            Text(
                                text = "Round ${race.round}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                            )

                    }
                }

                // Bottom Section: Date/Time OR Podium
                Column {
                    if (showDate || isCurrentWeekend) {
                        val (formattedDate, formattedTime) = remember(race.date, race.time) {
                            try {
                                val raceDate = LocalDate.parse(race.date)
                                val raceTime = race.time?.let { LocalTime.parse(it.take(5)) } ?: LocalTime.MIDNIGHT
                                val utcDateTime = LocalDateTime.of(raceDate, raceTime)
                                val zonedUtcDateTime = utcDateTime.atZone(ZoneId.of("UTC"))
                                val localZoneId = ZoneId.systemDefault()
                                val localZonedDateTime = zonedUtcDateTime.withZoneSameInstant(localZoneId)
                                val localDateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                                val localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                                val dateStr = localZonedDateTime.format(localDateFormatter)
                                val timeStr = localZonedDateTime.format(localTimeFormatter)
                                Pair(dateStr, timeStr.takeIf { race.time != null })
                            } catch (e: Exception) {
                                val fallbackTime = race.time?.let { "${it.take(5)} UTC" }
                                Pair(race.date, fallbackTime)
                            }
                        }

                        HorizontalDivider(
                            color = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.calendar_today_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                    contentDescription = "Race Date",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            formattedTime?.let { time ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.clock),
                                        contentDescription = "Race Time",
                                        modifier = Modifier.size(13.dp),
                                        tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = time,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    } else if (isPast && results.isNotEmpty()) {
                        val topThree = remember(results) { results.take(3) }

                        PodiumCard(
                            topThree = topThree,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumCard(
    topThree: List<ResultX>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            topThree.forEachIndexed { index, result ->
                PodiumDriverItem(
                    result = result,
                    position = index + 1,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PodiumDriverItem(
    result: ResultX,
    position: Int,
    modifier: Modifier = Modifier
) {
    val constructor = result.Constructor
    val constructorId = constructor.constructorId

    // Cache these expensive lookups
    val teamColorInfo = remember(constructorId) { getTeamColorInfo(constructorId) }
    val driverImage = remember(result.Driver.driverId) { getDriverImage(result.Driver.driverId) }

    val timeText = remember(position, result.Time.time) {
        if (position == 1) {
            result.Time.time
        } else {
            result.Time.time
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = position.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            Spacer(Modifier.width(4.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
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
                            .size(90) // Limit image size
                            .build(),
                        contentDescription = "${result.Driver.givenName} ${result.Driver.familyName}",
                        modifier = Modifier
                            .height(90.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))

            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = result.Driver.code ?: result.Driver.familyName.take(3).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun PodiumCardPreview() {
//    val topThreeDrivers = listOf(
//        ResultX(
//            "1", "1", "1", "25",
//            Driver("max_verstappen", "33", "VER", "", "Max", "Verstappen", "", "Dutch"),
//            Constructor("red_bull", "", "Red Bull", "Austrian"),
//            "1", "53", "Finished", Time("540161", "1:34:00.161"), ""
//        ),
//        ResultX(
//            "4", "2", "2", "18",
//            Driver("norris", "4", "NOR", "", "Lando", "Norris", "", "British"),
//            Constructor("mclaren", "", "McLaren", "British"),
//            "2", "53", "Finished", Time("959", "7.959"), ""
//        ),
//        ResultX(
//            "16", "3", "3", "15",
//            Driver("leclerc", "16", "LEC", "", "Charles", "Leclerc", "", "Monegasque"),
//            Constructor("ferrari", "", "Ferrari", "Italian"),
//            "3", "53", "Finished", Time("15370", "15.370"), ""
//        )
//    )
//    PodiumCard(topThree = topThreeDrivers)
//}

