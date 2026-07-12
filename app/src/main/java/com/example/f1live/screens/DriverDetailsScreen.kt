package com.example.f1live.screens

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.core.graphics.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.f1live.R
import com.example.f1live.api.RaceXXXX
import com.example.f1live.api.ResultXX
import com.example.f1live.api.UiState
import com.example.f1live.repository.DriverDImg
import com.example.f1live.repository.DriverDetailsImg
import com.example.f1live.repository.F1DriverImage
import com.example.f1live.viewmodel.F1ViewModel
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
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
import java.util.concurrent.TimeUnit
import kotlin.math.sign

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DriverDetailsScreen(
    season: String,
    driverId: String,
    viewModel: F1ViewModel = viewModel(),
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val driverDetailsState by viewModel.driverDetailsState.collectAsState()
    val hazeState = rememberHazeState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    LaunchedEffect(season, driverId) {
        viewModel.fetchDriverDetails(season, driverId)
    }

    val backdrop = rememberLayerBackdrop()

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

    when (val state = driverDetailsState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator(color = Color(0xFF2196F3))
            }
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading driver data",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        is UiState.Success -> {
            val races = state.data.MRData.RaceTable.Races
            val firstRace = races.firstOrNull()
            val driver = firstRace?.Results?.firstOrNull()?.Driver
            val constructor = firstRace?.Results?.firstOrNull()?.Constructor

            val driverImageUrl = driver?.let { driverData ->
                val fullName = "${driverData.givenName} ${driverData.familyName}"
                DriverDImg.drivers.find { driverImg ->
                    when (driverImg) {
                        is DriverDetailsImg -> driverImg.name == fullName
                        is F1DriverImage -> driverImg.name == fullName
                        else -> false
                    }
                }?.let { driverImg ->
                    when (driverImg) {
                        is DriverDetailsImg -> driverImg.imgUrl
                        is F1DriverImage -> driverImg.imgUrl
                        else -> null
                    }
                }
            }

            val totalPoints = races.sumOf { race ->
                race.Results.firstOrNull()?.points?.toIntOrNull() ?: 0
            }
            val totalRaces = races.size
            val finishedRaces = races.count { race ->
                race.Results.firstOrNull()?.status == "Finished"
            }
            val podiums = races.count { race ->
                val position = race.Results.firstOrNull()?.position?.toIntOrNull() ?: 0
                position in 1..3
            }

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .hazeSource(state = hazeState)
                        .layerBackdrop(backdrop),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF424242))
                            )

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(driverImageUrl ?: R.drawable.ethan_sexton_upwu8lfdj14_unsplash)
                                    .crossfade(true)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = "${driver?.givenName} ${driver?.familyName}",
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

                            driver?.let {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart)
                                        .padding(horizontal = 20.dp, vertical = 16.dp),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(top = 4.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        with(sharedTransitionScope) {
                                            Text(
                                                text = "${it.givenName}",
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                letterSpacing = 1.sp,
                                                modifier = Modifier.sharedBounds(
                                                    sharedTransitionScope.rememberSharedContentState(key = "race-name-${driver.givenName}"),
                                                    animatedVisibilityScope = animatedContentScope,
                                                    enter = fadeIn(),
                                                    exit = fadeOut(),
                                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                                )
                                            )
                                        }
                                        with(sharedTransitionScope) {
                                            Text(
                                                text = "${it.familyName}",
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                letterSpacing = 1.sp,
                                                modifier = Modifier.sharedBounds(
                                                    sharedTransitionScope.rememberSharedContentState(key = "race-name-${driver.familyName}"),
                                                    animatedVisibilityScope = animatedContentScope,
                                                    enter = fadeIn(),
                                                    exit = fadeOut(),
                                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                                )
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "#${it.permanentNumber}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "•",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f),
                                        )
                                        Text(
                                            text = constructor?.name ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f),
                                        )
                                        Text(
                                            text = "•",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f),
                                        )
                                        Text(
                                            text = it.nationality,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = "$season Season Stats",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight(700),
                                fontSize = 28.sp,
                                letterSpacing = 1.sp
                            )

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatCard(label = "Points", value = totalPoints.toString())
                                StatCard(label = "Races", value = totalRaces.toString())
                                StatCard(label = "Podiums", value = podiums.toString())
                                StatCard(label = "Finished", value = finishedRaces.toString())
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Race Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight(700),
                            fontSize = 28.sp,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp, start = 20.dp)
                        )
                    }

                    items(races) { race ->
                        val result = race.Results.firstOrNull()
                        result?.let {
                            RaceResultCard(race = race, result = it)
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
                                    if (l > 0f) lerp(4f.dp.toPx(), 3f.dp.toPx(), l)
                                    else lerp(4f.dp.toPx(), 3f.dp.toPx(), -l)
                                )
                                vibrancy()
                                lens(
                                    refractionHeight = 16f.dp.toPx(),
                                    refractionAmount = 36f.dp.toPx(),
                                    chromaticAberration = false,
                                    depthEffect = true
                                )
                            },
                            onDrawBackdrop = { drawBackdrop ->
                                drawBackdrop()
                                layer.record { drawBackdrop() }
                            }
                        )
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
}

@Composable
fun StatCard(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun RaceResultCard(
    race: RaceXXXX,
    result: ResultXX
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Race Name and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = race.raceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${race.Circuit.Location.locality}, ${race.Circuit.Location.country}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = race.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }

                // Position Badge
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = when (result.position.toIntOrNull()) {
                                1 -> Color(0xFFFFD700)
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> Color(0xFF424242)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.positionText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = if (result.position.toIntOrNull() in 1..3) Color.Black else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Race Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RaceDetail(label = "Points", value = result.points)
                RaceDetail(label = "Grid", value = result.grid)
                RaceDetail(label = "Laps", value = result.laps)
                RaceDetail(label = "Status", value = result.status)
            }

            // Time and Fastest Lap
            if (result.Time != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Use the new helper function here
                    RaceDetail(label = "Time", value = formatMillisToRaceTime(result.Time.millis))

                    result.FastestLap?.let {
                        RaceDetail(label = "Fastest Lap", value = it.Time.time)
                    }
                }
            } else {
                // For DNF/Disqualified races
                Spacer(modifier = Modifier.height(8.dp))
                result.FastestLap?.let {
                    RaceDetail(label = "Fastest Lap", value = it.Time.time)
                }
            }
        }
    }
}
fun formatMillisToRaceTime(millisStr: String?): String {
    // Return "N/A" or another placeholder if the time is null
    val totalMillis = millisStr?.toLongOrNull() ?: return "--:--.---"

    val hours = TimeUnit.MILLISECONDS.toHours(totalMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(totalMillis) % 60
    val milliseconds = totalMillis % 1000

    return if (hours > 0) {
        // Format as H:MM:SS.mmm if there are hours
        String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds)
    } else {
        // Format as MM:SS.mmm if less than an hour
        String.format("%02d:%02d.%03d", minutes, seconds, milliseconds)
    }
}
@Composable
fun RaceDetail(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 10.sp
        )
    }
}

