package com.example.f1live.screens

import android.os.Build
import android.view.VelocityTracker
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.containerColor
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.anhaki.picktime.PickDate
import com.anhaki.picktime.utils.PickTimeFocusIndicator
import com.anhaki.picktime.utils.PickTimeTextStyle
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.example.f1live.R
import com.example.f1live.api.ConstructorStanding
import com.example.f1live.api.ConstructorStandings
import com.example.f1live.api.DriverStanding
import com.example.f1live.api.DriverStandings
import com.example.f1live.api.RaceList
import com.example.f1live.api.RaceX
import com.example.f1live.api.UiState
import com.example.f1live.repository.Routes
import com.example.f1live.repository.logo
import com.example.f1live.viewmodel.F1ViewModel
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.CupertinoMaterials
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArchiveScreen(
    modifier: Modifier = Modifier,
    viewModel: F1ViewModel = viewModel(),
    navController: NavController
) {
    val raceState by viewModel.racesState.collectAsState()
    val driverStandingsState by viewModel.driverStandingsState.collectAsState()
    val constructorStandingsState by viewModel.constructorStandingsState.collectAsState()

    val selectedYear by viewModel.selectedYear.collectAsState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showYearPicker by remember { mutableStateOf(false) }

    // Tab setup
    val tabs = listOf("Schedule", "Drivers", "Teams")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    // Initial load when screen first opens
    LaunchedEffect(Unit) {
        // Load data for the default previous year on first composition
        viewModel.fetchRaces(selectedYear.toString(), forceRefresh = false)
        viewModel.fetchDriverStandings(selectedYear.toString(), forceRefresh = false)
        viewModel.fetchConstructorStandings(selectedYear.toString(), forceRefresh = false)
    }

    // Load data when year changes (excluding initial composition)
    LaunchedEffect(selectedYear) {
        // Skip the first emission as it's handled by LaunchedEffect(Unit)
        snapshotFlow { selectedYear }
            .drop(1) // Skip the initial value
            .collect { year ->
                viewModel.fetchRaces(year.toString(), forceRefresh = true)
                viewModel.fetchDriverStandings(year.toString(), forceRefresh = true)
                viewModel.fetchConstructorStandings(year.toString(), forceRefresh = true)
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = statusBarPadding)
    ) {
        // Header with year selector
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showYearPicker = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(30.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_drop_down_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                            contentDescription = "select year",
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "$selectedYear Season",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp
                    )
                }

                // TabRow with animated indicator
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

        // HorizontalPager for tab content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> ScheduleTabContent(
                    raceState = raceState,
                    selectedYear = selectedYear,
                    navController = navController,
                    isManualRefreshing = false,
                    onRefresh = {} // Empty since we removed refresh functionality
                )
                1 -> ArchieveDriverStandingsContent(driverStandingsState, navController, selectedYear.toString())
                2 -> ArchieveConstructorStandingsContent(constructorStandingsState)
            }
        }
    }

    // Year Picker Dialog
    if (showYearPicker) {
        var tempPickerYear by remember(selectedYear) { mutableStateOf(selectedYear) }

        Dialog(
            onDismissRequest = {
                showYearPicker = false
            }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.95f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                YearPickerContent(
                    selectedYear = tempPickerYear,
                    onYearSelected = { newYear ->
                        tempPickerYear = newYear
                    },
                    onDismiss = {
                        showYearPicker = false
                        // Only update if year actually changed
                        if (tempPickerYear != selectedYear) {
                            viewModel.setSelectedYear(tempPickerYear)
                            // LaunchedEffect(selectedYear) will automatically fetch the data
                        }
                    }
                )
            }
        }
    }
}





@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScheduleTabContent(
    raceState: UiState<RaceList>,
    selectedYear: Int,
    navController: NavController,
    isManualRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val bottomNavPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    PullToRefreshBox(
        isRefreshing = isManualRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 24.dp + bottomNavPadding + 80.dp
            )
        ) {
            when (val state = raceState) {
                is UiState.Loading -> {
                    if (!isManualRefreshing) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingIndicator(
                                    color = Color(0xFF2196F3)
                                )
                            }
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
                                text = "Error loading races. Please try again.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                is UiState.Success -> {
                    val races = state.data.MRData.RaceTable.Races
                    if (races.isEmpty()) {
                        item {
                            Text(
                                text = "No races found for the $selectedYear season.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(races) { race ->
                            ArchieveRaceCard(
                                race = race,
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function for smooth indicator animation
private fun lerp(start: Dp, stop: Dp, fraction: Float): Dp {
    return start + (stop - start) * fraction
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YearWheelPicker(
    modifier: Modifier = Modifier,
    startYear: Int = 1950,
    endYear: Int = LocalDate.now().year - 1,
    initialYear: Int = LocalDate.now().year - 1,
    onYearSelected: (Int) -> Unit = {},
    itemHeight: Dp = 50.dp,
    visibleItemsCount: Int = 5,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedTextColor: Color = Color(0xFFE10600),
    dividerColor: Color = Color(0xFFE10600).copy(alpha = 0.3f)
) {
    val years = remember(startYear, endYear) {
        (endYear downTo startYear).toList()
    }

    val itemHeightPx = with(LocalDensity.current) {
        itemHeight.toPx()
    }

    val totalHeight = itemHeight * visibleItemsCount
    val padding = (totalHeight - itemHeight) / 2

    val initialIndex = remember(initialYear) {
        (years.indexOf(initialYear).takeIf { it != -1 } ?: 0)
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Get haptic feedback
    val hapticFeedback = LocalHapticFeedback.current

    // Calculate smooth fractional offset for items
    val selectedIndexFloat by remember {
        derivedStateOf {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val firstVisibleOffset = listState.firstVisibleItemScrollOffset
            firstVisibleIndex + (firstVisibleOffset / itemHeightPx)
        }
    }

    val selectedIndex by remember {
        derivedStateOf {
            selectedIndexFloat.roundToInt().coerceIn(0, years.size - 1)
        }
    }

    // Trigger haptic feedback when selected index changes
    LaunchedEffect(selectedIndex) {
        if (listState.isScrollInProgress) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        onYearSelected(years[selectedIndex])
    }

    Box(
        modifier = modifier
            .height(totalHeight)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Gradient overlay for fade effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE10600),
                                Color.Transparent,
                                Color.Transparent,
                                Color(0xFF13161C)
                            ),
                            startY = 0f,
                            endY = size.height
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
        )

        // Year items in a LazyColumn
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(years) { index, year ->
                // Calculate smooth offset using fractional position
                val offset = index - selectedIndexFloat
                val absOffset = abs(offset)

                // Smooth interpolation for scale
                val scale = when {
                    absOffset <= 1f -> lerp(1f, 0.85f, absOffset)
                    absOffset <= 2f -> lerp(0.85f, 0.7f, absOffset - 1f)
                    else -> lerp(0.7f, 0.6f, (absOffset - 2f).coerceAtMost(1f))
                }

                // Smooth interpolation for alpha
                val alpha = when {
                    absOffset <= 1f -> lerp(1f, 0.6f, absOffset)
                    absOffset <= 2f -> lerp(0.6f, 0.4f, absOffset - 1f)
                    else -> lerp(0.4f, 0.2f, (absOffset - 2f).coerceAtMost(1f))
                }

                // Smooth color transition
                val color = androidx.compose.ui.graphics.lerp(
                    selectedTextColor,
                    textColor,
                    absOffset.coerceAtMost(1f)
                )

                // Smooth font weight transition
                val isCenterItem = absOffset < 0.5f

                // Animate the scale and alpha changes
                val animatedScale by animateFloatAsState(
                    targetValue = scale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "scale"
                )

                val animatedAlpha by animateFloatAsState(
                    targetValue = alpha,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutSlowInEasing
                    ),
                    label = "alpha"
                )

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = (24 * animatedScale).sp
                        ),
                        fontWeight = if (isCenterItem) FontWeight.Bold else FontWeight.Normal,
                        color = color,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .alpha(animatedAlpha)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
                    )
                }
            }
        }

        // Selection indicator (horizontal lines)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .padding(horizontal = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(dividerColor)
                    .align(Alignment.TopCenter)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(dividerColor)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

// Helper function for linear interpolation
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}

// Usage example in your ModalBottomSheet
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun YearPickerContent(
    modifier: Modifier = Modifier,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit // This will now be our "Done" button
) {
    // This column will be the content of our Dialog
    Column(
        modifier = modifier
            .fillMaxWidth()
            // Removed the navigationBars padding, not needed for a dialog
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {



        // Top bar with Title and Done button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Season",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onDismiss) { // "onDismiss" is now our "Done" action
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // The improved wheel picker
        YearWheelPicker(
            startYear = 1950,
            endYear = LocalDate.now().year - 1,
            initialYear = selectedYear,
            onYearSelected = onYearSelected,
            modifier = Modifier.fillMaxWidth()
        )

        // Add some space at the bottom
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArchieveRaceCard(
    race: RaceX,
    navController: NavController,
) {
    val cardShape = MaterialTheme.shapes.medium

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    Routes.F1GrandPrixArchieve.createRoute3(race.season, race.round)
                )
            },
        shape = cardShape,
        colors = CardDefaults.cardColors(Color.Transparent),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
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
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(
                            Routes.F1GrandPrixArchieve.createRoute3(race.season, race.round)
                        )
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF333639).copy(0.7f),
                            contentColor = Color.White
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_outward_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = "Read article",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }

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

                        Text(
                            text = "Round ${race.round}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = race.raceName,
                            style = MaterialTheme.typography.titleLargeEmphasized,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = race.Circuit.circuitName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${race.Circuit.Location.locality}, ${race.Circuit.Location.country}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Column {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
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
                                text = race.date,
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
                }


            }

        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArchieveDriverStandingsContent(state: UiState<DriverStandings>,navController: NavController,year: String) {
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
                contentPadding = PaddingValues(start = 16.dp,
                    end = 16.dp,
                    top = 24.dp,
                    bottom = 24.dp + bottomNavPadding + 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(standings) { standing ->
                    ArchieveDriverStandingCard(standing, navController, year)
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
fun ArchieveDriverStandingCard(standing: DriverStanding,navController: NavController,year: String) {
    // Get all the data needed for the card
    val constructor = standing.Constructors.firstOrNull()
    val constructorId = constructor?.constructorId ?: ""
    val constructorName = constructor?.name ?: ""
    val driverId = standing.Driver.driverId

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
            .clickable{
                navController.navigate(Routes.Driver.createRoute2(year,driverId))
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Position
                Box(
                    modifier = Modifier.width(45.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = standing.position,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

//                // 2. Driver Image
//                Box(
//                    modifier = Modifier
//                        .size(55.dp)
//                        .clip(CircleShape)
//                        .background(teamColorInfo.darkVariant),
//                    contentAlignment = Alignment.Center
//                ) {
//                    AsyncImage(
//                        model = ImageRequest.Builder(LocalContext.current)
//                            .data(driverImage)
//                            .crossfade(300)
//                            .memoryCachePolicy(CachePolicy.ENABLED)
//                            .diskCachePolicy(CachePolicy.ENABLED)
//                            .build(),
//                        contentDescription = "${standing.Driver.givenName} ${standing.Driver.familyName}",
//                        modifier = Modifier
//                            .height(90.dp)
//                            .fillMaxWidth(),
//                        contentScale = ContentScale.Crop,
//                        alignment = Alignment.TopCenter
//                    )
//                }

                Spacer(modifier = Modifier.width(16.dp))

                // 3. Driver and Team Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Driver Name
                    Text(
                        text = standing.Driver.givenName,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = standing.Driver.familyName.uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Team Logo and Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        AsyncImage(
//                            model = ImageRequest.Builder(LocalContext.current)
//                                .data(teamLogo)
//                                .crossfade(300)
//                                .memoryCachePolicy(CachePolicy.ENABLED)
//                                .diskCachePolicy(CachePolicy.ENABLED)
//                                .build(),
//                            contentDescription = "$constructorName Logo",
//                            modifier = Modifier
//                                .height(16.dp)
//                                .padding(end = 6.dp),
//                            contentScale = ContentScale.Fit
//                        )
                        Text(
                            text = constructorName,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

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
                            text = standing.points,
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
                        modifier = Modifier.size(56.dp)
                    )

                }
            }
        }
    }
}
@Composable
fun ArchieveConstructorStandingsContent(state: UiState<ConstructorStandings>) {
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
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 24.dp,
                    bottom = 24.dp + bottomNavPadding + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(standings) { standing ->
                    ArchieveConstructorStandingCard(standing)
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
fun ArchieveConstructorStandingCard(standing: ConstructorStanding) {
    // These are no longer needed for this card style
    // val teamColorInfo = getTeamColorInfo(standing.Constructor.constructorId)
    // val carImage = getCarImage(standing.Constructor.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp), // <-- Changed from fixed height
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp), // <-- Matched driver card shape
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // <-- Matched driver card elevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background( // <-- Replaced team background with driver card background
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1E1E1E),
                            Color(0xFF2A2A2A).copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Use the same Row structure as the driver card
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp), // <-- Matched driver card padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Position (Copied from driver card)
                Box(
                    modifier = Modifier.width(45.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = standing.position,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                // Use the same spacing as the driver card (after the commented-out image)
                Spacer(modifier = Modifier.width(16.dp))

                // 2. Constructor Name
                Column(
                    modifier = Modifier.weight(1f), // Takes up the available space
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = standing.Constructor.name.uppercase(),
                        fontSize = 18.sp, // Matched driver family name style
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2 // Allow two lines for longer team names
                    )
                }

                // Spacer before points
                Spacer(modifier = Modifier.width(12.dp))

                // 3. Points (Copied from driver card)
                Box(
                    modifier = Modifier.width(65.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = standing.points,
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
            }
        }
    }
}