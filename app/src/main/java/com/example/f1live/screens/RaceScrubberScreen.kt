package com.example.f1live.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.f1live.R
import com.example.f1live.api.Lap

// The UI-friendly data class


import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.roundToInt
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

// --- Animated Leaderboard (replaces DriverPositionChart) ---

@Composable
fun DriverLeaderboard(
    laps: List<com.example.f1live.api.Lap>,
    currentLap: Int,
    modifier: Modifier = Modifier
) {
    if (laps.isEmpty() || currentLap < 1 || currentLap > laps.size) return

    val currentTimings = laps[currentLap - 1].Timings
    val previousTimings = if (currentLap > 1) laps[currentLap - 2].Timings else null

    // Build driver states for this lap, sorted P1..P20
    val driverStates = remember(currentLap, laps) {
        currentTimings.map { timing ->
            val currentPos = timing.position.toIntOrNull() ?: 0
            val previousPos = previousTimings
                ?.find { it.driverId == timing.driverId }
                ?.position?.toIntOrNull() ?: currentPos

            val delta = when {
                currentPos < previousPos -> PositionDelta.UP
                currentPos > previousPos -> PositionDelta.DOWN
                else -> PositionDelta.STATIC
            }

            LapDriverState(
                driverId = timing.driverId,
                driverCode = timing.driverId.take(3).uppercase(),
                currentPosition = currentPos,
                positionChange = delta,
                lapTime = timing.time
            )
        }.sortedBy { it.currentPosition }
    }

    // Stable color per driver (same hue regardless of current rank)
    val allDriverIds = remember(laps) {
        laps.flatMap { it.Timings.map { t -> t.driverId } }.distinct().sorted()
    }
    val driverColors = remember(allDriverIds) {
        allDriverIds.mapIndexed { i, id ->
            val hue = i * 360f / allDriverIds.size.coerceAtLeast(1)
            id to Color.hsv(hue, 0.75f, 0.95f)
        }.toMap()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val driverCount = driverStates.size.coerceAtLeast(1)
        val rowSpacing = 4.dp
        // Total available height minus the gaps between rows, divided evenly
        val rowHeight = (maxHeight - rowSpacing * (driverCount - 1)) / driverCount

        driverStates.forEachIndexed { index, driver ->
            key(driver.driverId) {
                val targetOffsetY = index * (rowHeight + rowSpacing)

                val animatedOffsetY by animateDpAsState(
                    targetValue = targetOffsetY,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "posOffset_${driver.driverId}"
                )

                DriverLeaderboardRow(
                    driver = driver,
                    color = driverColors[driver.driverId] ?: Color.White,
                    rowHeight = rowHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = animatedOffsetY)
                )
            }
        }
    }
}

@Composable
fun DriverLeaderboardRow(
    driver: LapDriverState,
    color: Color,
    rowHeight: Dp,
    modifier: Modifier = Modifier
) {
    // Subtle flash on the accent bar when position changes
    val accentColor by animateColorAsState(
        targetValue = when (driver.positionChange) {
            PositionDelta.UP -> Color(0xFF00D26A)
            PositionDelta.DOWN -> Color(0xFFF8312F)
            PositionDelta.STATIC -> color
        },
        animationSpec = tween(400),
        label = "accentColor_${driver.driverId}"
    )

    Row(
        modifier = modifier
            .height(rowHeight)
            .background(Color(0xFF151515), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Left accent bar, colored by driver, flashes green/red on change
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(0.6f)
                    .background(accentColor, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "${driver.currentPosition}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(28.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column {
                Text(
                    text = driver.driverCode,
                    color = color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = driver.lapTime,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        when (driver.positionChange) {
            PositionDelta.UP -> Text("▲", color = Color(0xFF00D26A), fontWeight = FontWeight.Black, fontSize = 14.sp)
            PositionDelta.DOWN -> Text("▼", color = Color(0xFFF8312F), fontWeight = FontWeight.Black, fontSize = 14.sp)
            PositionDelta.STATIC -> Text("—", color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
// --- UI State Models ---
data class LapDriverState(
    val driverId: String,
    val driverCode: String,
    val currentPosition: Int,
    val positionChange: PositionDelta,
    val lapTime: String
)

enum class PositionDelta { UP, DOWN, STATIC }

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RaceScrubberScreen(
    laps: List<com.example.f1live.api.Lap>, // Ensure this imports your specific Lap data class
    navController: NavController
) {
    val totalLaps = laps.size
    var currentLapNumber by remember { mutableIntStateOf(if (totalLaps > 0) 1 else 0) }

    // --- Data Processing Logic ---
    // This function calculates exactly who moved up or down compared to the previous lap
    val getDriversForLap: (Int) -> List<LapDriverState> = remember(laps) {
        { lapNum ->
            if (laps.isEmpty() || lapNum < 1 || lapNum > laps.size) {
                emptyList()
            } else {
                val currentLapData = laps[lapNum - 1].Timings
                val previousLapData = if (lapNum > 1) laps[lapNum - 2].Timings else null

                currentLapData.map { timing ->
                    val currentPos = timing.position.toIntOrNull() ?: 0
                    val previousPos = previousLapData?.find { it.driverId == timing.driverId }?.position?.toIntOrNull() ?: currentPos

                    val delta = when {
                        currentPos < previousPos -> PositionDelta.UP
                        currentPos > previousPos -> PositionDelta.DOWN
                        else -> PositionDelta.STATIC
                    }

                    LapDriverState(
                        driverId = timing.driverId,
                        // Fallback: If you don't have the 3-letter code in the Laps endpoint, format the ID
                        driverCode = timing.driverId.take(3).uppercase(),
                        currentPosition = currentPos,
                        positionChange = delta,
                        lapTime = timing.time
                    )
                }.sortedBy { it.currentPosition } // Ensure they are ordered P1 to P20
            }
        }
    }

    val currentDriverList = remember(currentLapNumber, laps) {
        getDriversForLap(currentLapNumber)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lap Telemetry", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 8.dp).clip(CircleShape).background(Color(0xFF1E1E1E))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        if (laps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE10600))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // 1. The Timeline Scrubber
                LapTimelineScrubber(
                    currentLap = currentLapNumber,
                    totalLaps = totalLaps,
                    onLapChanged = { currentLapNumber = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. The Animated Leaderboard
                DriverLeaderboard(
                    laps = laps,
                    currentLap = currentLapNumber,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // takes remaining space in the Column below the scrubber
                )
            }
        }
    }
}

// --- Helper Composables ---

@Composable
fun LapTimelineScrubber(
    currentLap: Int,
    totalLaps: Int,
    onLapChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "LAP $currentLap",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "OF $totalLaps",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Slider(
            value = currentLap.toFloat(),
            onValueChange = { newValue ->
                val targetLap = newValue.roundToInt().coerceIn(1, totalLaps)
                if (targetLap != currentLap) {
                    onLapChanged(targetLap)
                }
            },
            valueRange = 1f..totalLaps.toFloat(),
            steps = if (totalLaps > 2) totalLaps - 2 else 0,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFE10600),
                activeTrackColor = Color(0xFFE10600),
                inactiveTrackColor = Color(0xFF2F2F2F),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DriverLapCard(driver: LapDriverState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF151515), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Side: Position and Name
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${driver.currentPosition}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(36.dp)
            )

            Column {
                Text(
                    text = driver.driverCode,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = driver.lapTime,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Right Side: Movement Indicator
        when (driver.positionChange) {
            PositionDelta.UP -> {
                Text(
                    text = "▲ UP",
                    color = Color(0xFF00D26A),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
            PositionDelta.DOWN -> {
                Text(
                    text = "▼ DOWN",
                    color = Color(0xFFF8312F),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
            PositionDelta.STATIC -> {
                Text(
                    text = "—",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}