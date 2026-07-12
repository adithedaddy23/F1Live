package com.example.f1live.screens

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
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
import androidx.core.graphics.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.f1live.R
import com.example.f1live.api.QualifyingResult
import com.example.f1live.api.RaceX
import com.example.f1live.api.Result
import com.example.f1live.api.ResultX
import com.example.f1live.api.SprintResult
import com.example.f1live.api.UiState
import com.example.f1live.repository.CircuitPhotos
import com.example.f1live.repository.Routes
import com.example.f1live.repository.TrackPhotos
import com.example.f1live.viewmodel.F1ViewModel
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.IntBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.sign

@OptIn(ExperimentalFoundationApi::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun F1GrandPrixScreen(
    season: String,
    round: String,
    viewModel: F1ViewModel = viewModel(),
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val resultState by viewModel.resultsState.collectAsState()
    val racesByRoundState by viewModel.racesRoundState.collectAsState()
    val qualifyingState by viewModel.qualifyingState.collectAsState()
    val sprintState by viewModel.sprintState.collectAsState()
    val hazeState = rememberHazeState()

    LaunchedEffect(season, round) {
        viewModel.fetchAllDataForRace(season, round)
        viewModel.fetchRacesByRound(season, round)
    }

    val backdrop = rememberLayerBackdrop()
    val animationScope = rememberCoroutineScope()
    val progressAnimation = remember { Animatable(0f) }

    // Adaptive luminance state for the icon button
    val layer = rememberGraphicsLayer()
    val luminanceAnimation = remember { Animatable(0f) }
    val iconColorAnimation = remember { Animatable(Color.White) }

    LaunchedEffect(layer) {
        val buffer = IntBuffer.allocate(25)
        while (isActive) {
            // Add delay to ensure layer is drawn before sampling
            delay(100)

            try {
                withContext(Dispatchers.IO) {
                    val imageBitmap = layer.toImageBitmap()
                    // Check if bitmap has valid dimensions
                    if (imageBitmap.width > 0 && imageBitmap.height > 0) {
                        val thumbnail = imageBitmap.asAndroidBitmap()
                            .scale(5, 5, false)
                            .copy(Bitmap.Config.ARGB_8888, false)
                        buffer.rewind()
                        thumbnail.copyPixelsToBuffer(buffer)

                        val averageLuminance = (0 until 25).sumOf { index ->
                            val color = buffer.get(index)
                            val r = (color shr 16 and 0xFF) / 255f
                            val g = (color shr 8 and 0xFF) / 255f
                            val b = (color and 0xFF) / 255f
                            0.2126 * r + 0.7152 * g + 0.0722 * b
                        } / 25

                        launch {
                            iconColorAnimation.animateTo(
                                if (averageLuminance > 0.5f) Color.Black else Color.White,
                                tween(800)
                            )
                        }
                        luminanceAnimation.animateTo(
                            averageLuminance.toFloat(),
                            tween(500)
                        )
                    }
                }
            } catch (e: Exception) {
                // Silently handle errors during bitmap creation
            }
        }
    }

    // Determine available tabs based on data
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

    when (racesByRoundState) {
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

            val trackImageResId = remember(race?.raceName, race?.Circuit?.circuitName) {
                TrackPhotos.getTrackList().firstOrNull { track ->
                    race?.raceName?.contains(track.gpName, ignoreCase = true) == true ||
                            race?.Circuit?.circuitName?.contains(track.circuitName, ignoreCase = true) == true
                }?.imgUrl
            }

            race?.let {
                val circuitImage = CircuitPhotos.getCircuitList().firstOrNull { circuit ->
                    race.raceName.contains(circuit.gpName, ignoreCase = true) ||
                            race.Circuit.circuitName.contains(circuit.circuitName, ignoreCase = true)
                }?.imgUrl

                Box(modifier = Modifier.fillMaxSize()) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeSource(state = hazeState)
                            .layerBackdrop(backdrop),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        // Header with Race Name
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            ) {

                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(trackImageResId ?: R.drawable._025_japanese_gp___race_start_2)
                                        .crossfade(true)
                                        .memoryCachePolicy(CachePolicy.ENABLED)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.4f)
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.6f),
                                                    Color.Black.copy(alpha = 0.9f)
                                                )
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart)
                                        .padding(horizontal = 20.dp, vertical = 16.dp),
                                ) {
                                    with(sharedTransitionScope) {
                                        Text(
                                            text = race.raceName,
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier
                                                .sharedBounds(
                                                rememberSharedContentState(key = "race-name-${race.round}"),
                                                animatedVisibilityScope = animatedContentScope,
                                                enter = fadeIn(),
                                                exit = fadeOut(),
                                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                            )
//                                                .renderInSharedTransitionScopeOverlay()
                                        )
                                    }
                                    with(sharedTransitionScope) {
                                        Text(
                                            text = race.Circuit.circuitName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f),
                                            modifier = Modifier.sharedBounds(
                                                sharedTransitionScope.rememberSharedContentState(key = "circuit-name-$round"),
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
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f),
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .sharedBounds(
                                                    sharedTransitionScope.rememberSharedContentState(key = "location-$round"),
                                                    animatedVisibilityScope = animatedContentScope,
                                                    enter = fadeIn(),
                                                    exit = fadeOut(),
                                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                                )
                                        )
                                    }
                                }
                            }
                        }

                        // Schedule Item
                        item {
                            when (racesByRoundState) {
                                is UiState.Success -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 16.dp)
                                    ) {
                                        Text(
                                            text = "Schedule",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight(700),
                                            fontSize = 28.sp,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(Modifier.height(12.dp))

                                        race.FirstPractice?.let { fp1 ->
                                            ScheduleItem(
                                                sessionName = "PRACTICE 1",
                                                date = fp1.date,
                                                time = fp1.time
                                            )
                                        }

                                        race.SecondPractice?.let { fp2 ->
                                            ScheduleItem(
                                                sessionName = "PRACTICE 2",
                                                date = fp2.date,
                                                time = fp2.time
                                            )
                                        }

                                        race.ThirdPractice?.let { fp3 ->
                                            ScheduleItem(
                                                sessionName = "PRACTICE 3",
                                                date = fp3.date,
                                                time = fp3.time
                                            )
                                        }

                                        race.Sprint?.let { sprint ->
                                            ScheduleItem(
                                                sessionName = "SPRINT",
                                                date = sprint.date,
                                                time = sprint.time
                                            )
                                        }

                                        race.Qualifying?.let { quali ->
                                            ScheduleItem(
                                                sessionName = "QUALIFYING",
                                                date = quali.date,
                                                time = quali.time
                                            )
                                        }

                                        ScheduleItem(
                                            sessionName = "RACE",
                                            date = race.date,
                                            time = race.time
                                        )
                                    }
                                }
                                is UiState.Loading -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }
                                is UiState.Error -> {
                                    Text(
                                        text = "DEBUG: Error loading schedule:",
                                        color = Color.Red,
                                        modifier = Modifier.padding(20.dp)
                                    )
                                }
                            }
                        }

                        // Circuit Image
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(220.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(circuitImage ?: R.drawable._025_japanese_gp___race_start_2)
                                            .crossfade(true)
                                            .memoryCachePolicy(CachePolicy.ENABLED)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .build(),
                                        contentDescription = race.Circuit.circuitName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit,
                                        alignment = Alignment.Center
                                    )
                                }
                            }
                        }

//                        item {
//                            OutlinedButton(
//                                onClick = {
//                                    navController.navigate(Routes.RaceScrubber.createRoute(season, round))
//                                },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 20.dp, vertical = 8.dp)
//                                    .height(56.dp),
//                                shape = RoundedCornerShape(12.dp),
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    contentColor = Color(0xFFE10600), // Iconic F1 Red
//                                    containerColor = Color(0xFFE10600).copy(alpha = 0.05f) // Very faint red background
//                                ),
//                                border = BorderStroke(1.dp, Color(0xFFE10600).copy(alpha = 0.5f))
//                            ) {
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.Center
//                                ) {
//                                    // Optional: Add a standard Material play icon if you have one imported
//                                    // Icon(
//                                    //     painter = painterResource(id = R.drawable.ic_play_arrow),
//                                    //     contentDescription = null,
//                                    //     modifier = Modifier.padding(end = 8.dp)
//                                    // )
//
//                                    Text(
//                                        text = "INTERACTIVE LAP REPLAY",
//                                        style = MaterialTheme.typography.labelLarge,
//                                        fontWeight = FontWeight.Black,
//                                        letterSpacing = 1.5.sp
//                                    )
//                                }
//                            }
//                        }

                        // Conditionally show results
                        if (availableTabs.isNotEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
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
                                            val targetTab = tabPositions.getOrNull(pagerState.targetPage) ?: currentTab

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
                                                RaceResultsContent(
                                                    results = raceResults,
                                                    modifier = Modifier.padding(top = 16.dp)
                                                )
                                            }
                                        }
                                        "Qualifying" -> {
                                            if (qualifyingState is UiState.Success) {
                                                val qualifyingResults = (qualifyingState as UiState.Success)
                                                    .data.MRData.RaceTable.Races.firstOrNull()?.QualifyingResults ?: emptyList()
                                                QualifyingResultsContent(
                                                    results = qualifyingResults,
                                                    modifier = Modifier.padding(top = 16.dp)
                                                )
                                            }
                                        }
                                        "Sprint" -> {
                                            if (sprintState is UiState.Success) {
                                                val sprintResults = (sprintState as UiState.Success)
                                                    .data.MRData.RaceTable.Races.firstOrNull()?.SprintResults ?: emptyList()
                                                SprintResultsContent(
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

                    // Floating Back Button with Adaptive Luminance
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = statusBarPadding)
                            .padding(top = 16.dp, start = 16.dp)
                            .clip(CircleShape)
                            .size(50.dp)
                            .background(color = Color.Black, shape = CircleShape)
                            .graphicsLayer {
                                val progress = progressAnimation.value
                                val maxScale = (size.width + 16f.dp.toPx()) / size.width
                                val scale = lerp(1f, maxScale, progress)
                                scaleX = scale
                                scaleY = scale
                            }
                            .drawBackdrop(
                                backdrop = backdrop,
                                shape = { CircleShape },
                                effects = {
                                    val l = (luminanceAnimation.value * 2f - 1f).let {
                                        sign(it) * it * it
                                    }

                                    colorControls(
                                        brightness = if (l > 0f) lerp(0.1f, 0.5f, l)
                                        else lerp(0.1f, -0.2f, -l),
                                        contrast = if (l > 0f) lerp(1f, 0f, l) else 1f,
                                        saturation = 1.5f
                                    )

                                    blur(
                                        if (l > 0f) lerp(8f.dp.toPx(), 16f.dp.toPx(), l)
                                        else lerp(8f.dp.toPx(), 2f.dp.toPx(), -l)
                                    )

                                    vibrancy()

                                    lens(
                                        refractionHeight = 12f.dp.toPx(),
                                        refractionAmount = 20f.dp.toPx(),
                                        chromaticAberration = true,
                                        depthEffect = true
                                    )
                                },
                                onDrawBackdrop = { drawBackdrop ->
                                    drawBackdrop()
                                    layer.record { drawBackdrop() }
                                }
                            )
                            .clickable {}
                            .pointerInput(animationScope) {
                                val animationSpec = spring(0.5f, 300f, 0.001f)
                                awaitEachGesture {
                                    awaitFirstDown()
                                    animationScope.launch {
                                        progressAnimation.animateTo(1f, animationSpec)
                                    }

                                    waitForUpOrCancellation()
                                    animationScope.launch {
                                        progressAnimation.animateTo(0f, animationSpec)
                                    }
                                }
                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                            contentDescription = "Back",
                            tint = iconColorAnimation.value
                        )
                    }
                }
            }
        }
        else -> {}
    }
}

@Composable
fun ScheduleItem(
    sessionName: String,
    date: String?,
    time: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = sessionName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
//            Icon(
//                imageVector = Icons.Default.DateRange,
//                contentDescription = "Date",
//                modifier = Modifier.size(16.dp),
//                tint = Color.White.copy(alpha = 0.7f)
//            )
            Spacer(Modifier.width(6.dp))

            date?.let {
                Text(
                    text = formatDate(it),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            time?.let {
                Text(
                    text = " • ${formatTime(it)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// Helper function to format date
fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

// Helper function to format time
fun formatTime(timeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("HH:mm:ss'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = inputFormat.parse(timeString)
        time?.let { outputFormat.format(it) } ?: timeString.substringBefore(":")
    } catch (e: Exception) {
        timeString.substringBefore("Z").substringBefore(":")
    }
}

@Composable
fun RaceResultsContent(
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
            RaceResultCard(result = result)
        }
    }
}

@Composable
fun RaceResultCard(result: ResultX) {
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
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
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

                Spacer(modifier = Modifier.width(8.dp))

                // Driver Image
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(teamColorInfo.darkVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(driverImage)
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "${result.Driver.givenName} ${result.Driver.familyName}",
                        modifier = Modifier
                            .height(90.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // --- MODIFIED SECTION START ---
                // Driver Code and Team Info are now in a vertical Column
                Column(
                    modifier = Modifier.weight(1f), // This Column takes the available space
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Driver Code
                    Text(
                        text = result.Driver.code ?: result.Driver.familyName.take(3).uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                        // Removed fixed width
                    )

                    // Team Logo and Name (directly below the code)
                    Column(
                        // This inner Column just groups the logo and name
                        verticalArrangement = Arrangement.Center
                    ) {
//                        AsyncImage(
//                            model = teamLogo,
//                            contentDescription = "${constructor.name} Logo",
//                            modifier = Modifier
//                                .height(14.dp)
//                                .padding(bottom = 4.dp),
//                            contentScale = ContentScale.Fit
//                        )
                        Text(
                            text = constructor.name,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                // --- MODIFIED SECTION END ---

                Spacer(modifier = Modifier.width(8.dp))

                // Time
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(70.dp)
                ) {
                    Text(
                        text = result.Time?.time ?: result.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (result.Time != null) Color.White else Color.Red.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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

                Spacer(modifier = Modifier.width(8.dp))

                // Points
                Box(
                    modifier = Modifier.width(45.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
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
}
@Composable
fun QualifyingResultsContent(
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
            QualifyingResultCard(result = result)
        }
    }
}

@Composable
fun QualifyingResultCard(result: QualifyingResult) {
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
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

                Spacer(modifier = Modifier.width(8.dp))

                // Driver Image
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .background(teamColorInfo.darkVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(driverImage)
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "${result.Driver.givenName} ${result.Driver.familyName}",
                        modifier = Modifier
                            .height(90.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Driver Code
                Text(
                    text = result.Driver.code ?: result.Driver.familyName.take(3).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp,
                    modifier = Modifier.width(50.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Team Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = teamLogo,
                        contentDescription = "${constructor.name} Logo",
                        modifier = Modifier
                            .height(14.dp)
                            .padding(bottom = 4.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = constructor.name,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Best Time (Q3 > Q2 > Q1)
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = result.Q3 ?: result.Q2 ?: result.Q1 ?: "N/A",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = when {
                            result.Q3 != null -> "Q3"
                            result.Q2 != null -> "Q2"
                            else -> "Q1"
                        },
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SprintResultsContent(
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
            SprintResultCard(result = result)
        }
    }
}

@Composable
fun SprintResultCard(result: SprintResult) {
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
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

                Spacer(modifier = Modifier.width(8.dp))

                // Driver Image
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .background(teamColorInfo.darkVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(driverImage)
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "${result.Driver.givenName} ${result.Driver.familyName}",
                        modifier = Modifier
                            .height(90.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Driver Code
                Text(
                    text = result.Driver.code ?: result.Driver.familyName.take(3).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp,
                    modifier = Modifier.width(50.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Team Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = teamLogo,
                        contentDescription = "${constructor.name} Logo",
                        modifier = Modifier
                            .height(14.dp)
                            .padding(bottom = 4.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = constructor.name,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Time
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(70.dp)
                ) {
                    Text(
                        text = result.Time?.time ?: result.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (result.Time != null) Color.White else Color.Red.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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

                Spacer(modifier = Modifier.width(8.dp))

                // Points
                Box(
                    modifier = Modifier.width(45.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
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
}

// Helper function for tab indicator animation
private fun lerp(start: Dp, stop: Dp, fraction: Float): Dp {
    return start + (stop - start) * fraction
}