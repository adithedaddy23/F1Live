package com.example.f1live.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.f1live.R
import com.example.f1live.api.QualifyingResult
import com.example.f1live.api.Result
import com.example.f1live.api.ResultX
import com.example.f1live.api.SprintResult
import com.example.f1live.api.UiState
import com.example.f1live.viewmodel.F1ViewModel
import com.kyant.backdrop.backdrops.layerBackdrop
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun F1GrandPrixArchieveScreen(
    season: String,
    round: String,
    viewModel: F1ViewModel = viewModel(),
    navController: NavController,
) {
    val resultState by viewModel.resultsState.collectAsState()
    val racesByRoundState by viewModel.racesRoundState.collectAsState()
    val qualifyingState by viewModel.qualifyingState.collectAsState()
    val sprintState by viewModel.sprintState.collectAsState()
    val hazeState = rememberHazeState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(season, round) {
        viewModel.fetchAllDataForRace(season, round)
        viewModel.fetchRacesByRound(season, round)
    }

    val availableTabs = remember(resultState, qualifyingState, sprintState) {
        buildList {
            if (resultState is UiState.Success) add("Race")
            if (qualifyingState is UiState.Success) add("Qualifying")
            if (sprintState is UiState.Success) add("Sprint")
        }
    }

    val pagerState = rememberPagerState(pageCount = { availableTabs.size })
    val coroutineScope = rememberCoroutineScope()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    when(racesByRoundState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = statusBarPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                LinearWavyProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading race data",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        is UiState.Success -> {
            val race = (racesByRoundState as UiState.Success).data.MRData.RaceTable.Races.firstOrNull()

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(
                                onClick = { navController.popBackStack() }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_back_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            scrolledContainerColor = MaterialTheme.colorScheme.background
                        ),
                        scrollBehavior = scrollBehavior,

                    )
                },
                containerColor = Color.Transparent
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .hazeSource(state = hazeState)
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 8.dp),
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                        ) {
                            Text(
                                text = race?.raceName ?: "Race name not available",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${race?.Circuit?.Location?.locality}, ${race?.Circuit?.Location?.country}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "${race?.season} Season",
                                style = MaterialTheme.typography.headlineMediumEmphasized,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    if (availableTabs.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Text(
                                    text = "Results",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight(700),
                                    fontSize = 28.sp,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                                TabRow(
                                    selectedTabIndex = pagerState.currentPage,
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White,
                                    indicator = { tabPositions ->
                                        val currentTab = tabPositions[pagerState.currentPage]
                                        val targetTab =
                                            tabPositions.getOrNull(pagerState.targetPage)
                                                ?: currentTab

                                        val indicatorLeft by animateDpAsState(
                                            targetValue = lerp(
                                                currentTab.left,
                                                targetTab.left,
                                                pagerState.currentPageOffsetFraction
                                            ),
                                            label = "indicator-left"
                                        )
                                        val indicatorRight by animateDpAsState(
                                            targetValue = lerp(
                                                currentTab.right,
                                                targetTab.right,
                                                pagerState.currentPageOffsetFraction
                                            ),
                                            label = "indicator-right"
                                        )

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
                                    availableTabs.forEachIndexed { index, title ->
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

                        item {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(600.dp)
                            ) { page ->
                                when (availableTabs[page]) {
                                    "Race" -> {
                                        if (resultState is UiState.Success) {
                                            val raceResults = (resultState as UiState.Success<Result>).data.MRData.RaceTable.Races.firstOrNull()?.Results ?: emptyList()
                                            ArchieveRaceResultsContent(
                                                results = raceResults,
                                                modifier = Modifier.padding(top = 16.dp)
                                            )
                                        }
                                    }
                                    "Qualifying" -> {
                                        if (qualifyingState is UiState.Success) {
                                            val qualifyingResults =
                                                (qualifyingState as UiState.Success)
                                                    .data.MRData.RaceTable.Races.firstOrNull()?.QualifyingResults
                                                    ?: emptyList()
                                            ArchieveQualifyingResultsContent(
                                                results = qualifyingResults,
                                                modifier = Modifier.padding(top = 16.dp)
                                            )
                                        }
                                    }
                                    "Sprint" -> {
                                        if (qualifyingState is UiState.Success) {
                                            val sprintResults = (sprintState as UiState.Success)
                                                .data.MRData.RaceTable.Races.firstOrNull()?.SprintResults
                                                ?: emptyList()
                                            ArchieveSprintResultsContent(
                                                results = sprintResults,
                                                modifier = Modifier.padding(top = 16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
private fun lerp(start: Dp, stop: Dp, fraction: Float): Dp {
    return start + (stop - start) * fraction
}

@Composable
fun ArchieveRaceResultsContent(
    results: List<ResultX>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(results) { result ->
            ArchieveRaceResultCard(result = result)
        }
    }
}

@Composable
fun ArchieveRaceResultCard(result: ResultX) {
    val constructor = result.Constructor
    val teamColorInfo = getTeamColorInfo(constructor.constructorId)
    val driverImage = getDriverImage(result.Driver.driverId)
    val teamLogo = getTeamLogo(constructor.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1E1E1E),
                            Color(0xFF2A2A2A).copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Position
                Box(
                    modifier = Modifier.width(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.position,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = when (result.position) {
                            "1" -> Color(0xFFFFD700)
                            "2" -> Color(0xFFC0C0C0)
                            "3" -> Color(0xFFCD7F32)
                            else -> Color.White
                        },
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Driver Code and Team Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = result.Driver.familyName.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        letterSpacing = 1.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        AsyncImage(
//                            model = teamLogo,
//                            contentDescription = "${constructor.name} Logo",
//                            modifier = Modifier.height(12.dp),
//                            contentScale = ContentScale.Fit
//                        )
                        Text(
                            text = constructor.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Time
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(80.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = result.Time?.time ?: result.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (result.Time != null) Color.White else Color.Red.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "Laps: ${result.laps}" ,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (result.Time != null) Color.White else Color.Red.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Points
                Column(
                    modifier = Modifier.width(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = result.points,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "PTS",
                        fontSize = 8.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
@Composable
fun ArchieveQualifyingResultsContent(
    results: List<QualifyingResult>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(results) { result ->
            ArchieveQualifyingResultCard(result = result)
        }
    }
}

@Composable
fun ArchieveQualifyingResultCard(result: QualifyingResult) {
    val constructor = result.Constructor
    val teamColorInfo = getTeamColorInfo(constructor.constructorId)
    val driverImage = getDriverImage(result.Driver.driverId)
    val teamLogo = getTeamLogo(constructor.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1E1E1E),
                            Color(0xFF2A2A2A).copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Position
                Box(
                    modifier = Modifier.width(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.position,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = when (result.position) {
                            "1" -> Color(0xFFFFD700)
                            "2" -> Color(0xFFC0C0C0)
                            "3" -> Color(0xFFCD7F32)
                            else -> Color.White
                        },
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.width(12.dp))

                // Driver Code and Team Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = result.Driver.familyName.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        AsyncImage(
//                            model = teamLogo,
//                            contentDescription = "${constructor.name} Logo",
//                            modifier = Modifier.height(12.dp),
//                            contentScale = ContentScale.Fit
//                        )
                        Text(
                            text = constructor.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Best Time (Q3 > Q2 > Q1)
                Column(
                    modifier = Modifier.width(90.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = result.Q3 ?: result.Q2 ?: result.Q1 ?: "N/A",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = when {
                            result.Q3 != null -> "Q3"
                            result.Q2 != null -> "Q2"
                            else -> "Q1"
                        },
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun ArchieveSprintResultsContent(
    results: List<SprintResult>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(results) { result ->
            ArchieveSprintResultCard(result = result)
        }
    }
}

@Composable
fun ArchieveSprintResultCard(result: SprintResult) {
    val constructor = result.Constructor
    val teamColorInfo = getTeamColorInfo(constructor.constructorId)
    val driverImage = getDriverImage(result.Driver.driverId)
    val teamLogo = getTeamLogo(constructor.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1E1E1E),
                            Color(0xFF2A2A2A).copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Position
                Box(
                    modifier = Modifier.width(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.position,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = when (result.position) {
                            "1" -> Color(0xFFFFD700)
                            "2" -> Color(0xFFC0C0C0)
                            "3" -> Color(0xFFCD7F32)
                            else -> Color.White
                        },
                        textAlign = TextAlign.Center
                    )
                }

                // Driver Code and Team Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = result.Driver.familyName.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        AsyncImage(
//                            model = teamLogo,
//                            contentDescription = "${constructor.name} Logo",
//                            modifier = Modifier.height(12.dp),
//                            contentScale = ContentScale.Fit
//                        )
                        Text(
                            text = constructor.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Time
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(80.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = result.Time?.time ?: result.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (result.Time != null) Color.White else Color.Red.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "Laps: ${result.laps}" ,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (result.Time != null) Color.White else Color.Red.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Points
                Column(
                    modifier = Modifier.width(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = result.points,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "PTS",
                        fontSize = 8.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}