package com.example.f1live

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.f1live.api.Circuit
import com.example.f1live.api.DriverStanding
import com.example.f1live.api.DriverStandings
import com.example.f1live.api.RaceX
import com.example.f1live.api.UiState
import com.example.f1live.bottomnavbar.BottomNavScreen
import com.example.f1live.bottomnavbar.bottomNavItems
import com.example.f1live.repository.Routes
import com.example.f1live.screens.ArchiveScreen
import com.example.f1live.screens.DriverDetailsScreen
import com.example.f1live.screens.DriversScreen
import com.example.f1live.screens.F1GrandPrixArchieveScreen
import com.example.f1live.screens.F1GrandPrixScreen
import com.example.f1live.screens.Homescreen
import com.example.f1live.screens.StandingsScreen
import com.example.f1live.ui.theme.F1LiveTheme
import com.example.f1live.utils.DampedDragAnimation
import com.example.f1live.utils.InteractiveHighlight
import com.example.f1live.viewmodel.F1ViewModel
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.FluentMaterials
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import androidx.navigation.NavDestination
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.f1live.api.Lap
import com.example.f1live.repository.ApkDownloader
import com.example.f1live.screens.NewsScreen
import com.example.f1live.screens.RaceScrubberScreen
import com.example.f1live.screens.UpdateDialog
import com.example.f1live.screens.WhatsNewDialog
import com.example.f1live.utils.LiquidBottomTabs
import com.example.f1live.viewmodel.UpdateState
import com.example.f1live.viewmodel.UpdateViewModel
import com.example.f1live.viewmodel.WhatsNewViewModel
import com.example.f1live.widget.F1WidgetUpdateWorker
import com.google.protobuf.LazyStringArrayList.emptyList
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.effects.colorControls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.nio.IntBuffer
import java.util.concurrent.TimeUnit
import kotlin.collections.emptyList

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val updateRequest = PeriodicWorkRequestBuilder<F1WidgetUpdateWorker>(
            6, TimeUnit.HOURS // Update every 6 hours
        )
            .setInitialDelay(0, TimeUnit.SECONDS) // Update immediately on first launch
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "F1WidgetUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            updateRequest
        )
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.Transparent.toArgb()
            )
        )
        setContent {
            F1LiveTheme {
                MainScreen()
            }
        }
    }
}



@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalSharedTransitionApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route


    // Create HazeState for both status bar and bottom bar blur
    val hazeState = rememberHazeState()

    // List of routes where bottom bar should be hidden
    val routesWithoutBottomBar = listOf(
        Routes.F1GrandPrix.routes,
        Routes.Driver.routes,
        Routes.F1GrandPrixArchieve.routes,
        Routes.RaceScrubber.routes // <--- ADD THIS HERE
    )

    val showBottomBar = routesWithoutBottomBar.none { it == currentRoute }
    val backdrop = rememberLayerBackdrop()

    val context = LocalContext.current
    val updateViewModel: UpdateViewModel = viewModel()
    val updateState by updateViewModel.state.collectAsState()

    val whatsNewViewModel: WhatsNewViewModel = viewModel()
    val whatsNewState by whatsNewViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        whatsNewViewModel.checkWhatsNew(BuildConfig.VERSION_NAME)
    }

    WhatsNewDialog(state = whatsNewState, onDismiss = { whatsNewViewModel.dismiss() })

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        updateViewModel.recheckPermission(context)
    }

    LaunchedEffect(Unit) {
        updateViewModel.checkForUpdate(BuildConfig.VERSION_NAME)
    }

    UpdateDialog(
        state = updateState,
        onDownloadClick = {
            val release = (updateState as? UpdateState.Available)?.release ?: return@UpdateDialog
            updateViewModel.startDownload(context, release)
        },
        onInstallClick = { uri ->
            ApkDownloader(context).installApk(uri)
            updateViewModel.dismiss()
        },
        onOpenSettingsClick = { _ ->
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            settingsLauncher.launch(intent)
        },
        onDismiss = { updateViewModel.dismiss() }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    // Pass the hazeState to BottomNavigationBar
                    BottomNavigationBar(
                        navController = navController,
                        hazeState = hazeState,
                        backdrop = backdrop
                    )
                }
            }
        ) { innerPadding ->
            SharedTransitionLayout {
                NavHost(
                    navController = navController,
                    startDestination = BottomNavScreen.Race.route,
                    modifier = Modifier
                        .fillMaxSize()
                        .layerBackdrop(backdrop)
//                    .padding(
//                        bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
//                    )
                        // Mark content as haze source
                        .hazeSource(state = hazeState),
                ) {
                    composable(BottomNavScreen.Race.route) {
                        Homescreen(
                            navController = navController,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable
                        )
                    }
                    composable(BottomNavScreen.Standings.route) {
                        StandingsScreen(
                            navController = navController,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable
                        )
                    }
//                    composable(BottomNavScreen.Drivers.route) {
//                        DriversScreen(
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
                    composable(BottomNavScreen.Archive.route) {
                        ArchiveScreen(navController = navController)
                    }


                    // Grand Prix screen with parameters
                    composable(
                        route = Routes.F1GrandPrix.routes,
                        arguments = listOf(
                            navArgument("season") { type = NavType.StringType },
                            navArgument("round") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val season = backStackEntry.arguments?.getString("season") ?: ""
                        val round = backStackEntry.arguments?.getString("round") ?: ""
                        F1GrandPrixScreen(
                            season = season,
                            round = round,
                            navController = navController,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable
                        )
                    }

                    composable(
                        route = Routes.RaceScrubber.routes,
                        arguments = listOf(
                            navArgument("season") { type = NavType.StringType },
                            navArgument("round") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val season = backStackEntry.arguments?.getString("season") ?: ""
                        val round = backStackEntry.arguments?.getString("round") ?: ""



                        val viewModel: F1ViewModel = viewModel()
                        LaunchedEffect(season, round) {
                            viewModel.fetchLapData(season, round)
                        }

                        // 1. Collect the lapDataState, not resultsState
                        val lapState by viewModel.lapDataState.collectAsState()

                        // 2. Safely extract the actual Lap list
                        val laps = if (lapState is UiState.Success) {
                            val raceLapData = (lapState as UiState.Success).data.MRData.RaceTable.Races.firstOrNull()
                            raceLapData?.Laps ?: emptyList()
                        } else {
                            emptyList()
                        }

                        RaceScrubberScreen(
                            laps = laps as List<Lap>,
                            navController = navController
                        )
                    }



                    composable(
                        route = Routes.F1GrandPrixArchieve.routes,
                        arguments = listOf(
                            navArgument("season") { type = NavType.StringType },
                            navArgument("round") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val season = backStackEntry.arguments?.getString("season") ?: ""
                        val round = backStackEntry.arguments?.getString("round") ?: ""
                        F1GrandPrixArchieveScreen(
                            season = season,
                            round = round,
                            navController = navController,
                        )
                    }

                    composable(
                        route = Routes.Driver.routes,
                        arguments = listOf(
                            navArgument("season") { type = NavType.StringType },
                            navArgument("driverId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val season = backStackEntry.arguments?.getString("season") ?: ""
                        val driverId = backStackEntry.arguments?.getString("driverId") ?: ""
                        DriverDetailsScreen(
                            season = season,
                            driverId = driverId,
                            navController = navController,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable
                        )
                    }

                    composable(BottomNavScreen.News.route) {
                        NewsScreen()
                    }
                }
            }

        }

        // Frosted glass status bar overlay
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .windowInsetsTopHeight(WindowInsets.statusBars)
//                .hazeEffect(
//                    state = hazeState,
//                    style = HazeMaterials.regular()
//                ) {
//                    blurRadius = 10.dp
//                    blurredEdgeTreatment = BlurredEdgeTreatment(CircleShape)
//                    progressive = HazeProgressive.verticalGradient(
//                        startIntensity = 0.5f,
//                        endIntensity = 0f
//                    )
//                }
//                .align(Alignment.TopCenter)
//        )
    }
}


@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomNavigationBar(
    navController: NavController,
    hazeState: HazeState,
    backdrop: Backdrop
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    // Find the index of currently selected tab
    val selectedTabIndex = bottomNavItems.indexOfFirst {
        it.route == currentDestination?.route
    }.takeIf { it != -1 } ?: 0

    // Check if device is running Android 13 (API 33) or higher
    val isAndroid13OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    if (isAndroid13OrAbove) {
        // Custom glassmorphic bottom nav for Android 13+
        CustomGlassmorphicBottomNav(
            navController = navController,
            currentDestination = currentDestination,
            backdrop = backdrop
        )
    } else {
        // Standard Material 3 bottom nav for Android 12 and below
        StandardBottomNav(
            navController = navController,
            currentDestination = currentDestination
        )
    }
}

@Composable
private fun StandardBottomNav(
    navController: NavController,
    currentDestination: NavDestination?
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 3.dp
    ) {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconRes),
                        contentDescription = screen.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = currentDestination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun CustomGlassmorphicBottomNav(
    navController: NavController,
    currentDestination: NavDestination?,
    backdrop: Backdrop
) {
    // Check if device is running Android 14 (API 34) or higher
    val isAndroid14OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    val selectedIconColorAnimation = remember { androidx.compose.animation.Animatable(Color(0xFF1565C0)) }
    // Adaptive luminance detection
    val layer = rememberGraphicsLayer()
    val luminanceAnimation = remember { Animatable(0.5f) }
    val notSelectedIconColorAnimation = remember { androidx.compose.animation.Animatable(Color(0xFFE0E0E0)) }

    LaunchedEffect(layer) {
        val buffer = IntBuffer.allocate(25)
        while (isActive) {
            withContext(Dispatchers.IO) {
                val imageBitmap = layer.toImageBitmap()
                val thumbnail = imageBitmap.asAndroidBitmap()
                    .scale(5, 5, false)
                    .copy(Bitmap.Config.ARGB_8888, false)
                buffer.rewind()
                thumbnail.copyPixelsToBuffer(buffer)
            }
            val averageLuminance = (0 until 25).sumOf { index ->
                val color = buffer.get(index)
                val r = (color shr 16 and 0xFF) / 255f
                val g = (color shr 8 and 0xFF) / 255f
                val b = (color and 0xFF) / 255f
                0.2126 * r + 0.7152 * g + 0.0722 * b
            } / 25
            launch {
                notSelectedIconColorAnimation.animateTo(
                    if (averageLuminance > 0.5f) Color(0xFF13161C) else Color(0xFFE0E0E0),
                    tween(800)
                )
            }


            luminanceAnimation.animateTo(
                averageLuminance.toFloat(),
                tween(400)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 26.dp, top = 16.dp, start = 38.dp, end = 38.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = ContinuousCapsule,
                    ambientColor = Color.White.copy(alpha = 0.15f),
                    spotColor = Color.White.copy(alpha = 0.25f)
                )
                .then(
                    if (isAndroid14OrAbove) {
                        Modifier.drawBackdrop(
                            backdrop = backdrop,
                            shape = { ContinuousCapsule },
                            effects = {
                                // Adaptive effects based on luminance
                                val l = (luminanceAnimation.value * 2f - 1f).let {
                                    sign(it) * it * it
                                }
                                colorControls(
                                    brightness = if (l > 0f) lerp(0.1f, 0.5f, l)
                                    else lerp(0.1f, -0.2f, -l),
                                    contrast = if (l > 0f) lerp(1f, 0f, l)
                                    else 1f,
                                    saturation = 1.3f,
                                )

                                blur(
                                    if (l > 0f) lerp(3f.dp.toPx(), 2.5f.dp.toPx(), l)
                                    else lerp(3f.dp.toPx(), 2.5f.dp.toPx(), -l),
                                    edgeTreatment = TileMode.Clamp
                                )

                                lens(
                                    20f.dp.toPx(),
                                    38f.dp.toPx(),
                                    chromaticAberration = false,
                                    depthEffect = false,
                                )

                            },
                            onDrawBackdrop = { drawBackdrop ->
                                drawBackdrop()
                                layer.record { drawBackdrop() }
                            }
                        )
                    } else {
                        Modifier.background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            ContinuousCapsule
                        )
                    }
                )
                .clip(RoundedCornerShape(32.dp))
        ) {
            // Bottom navigation items
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEachIndexed { index, screen ->
                    val selected = currentDestination?.route == screen.route

                    val alpha by animateFloatAsState(
                        targetValue = if (selected) 1f else 0.8f,
                        label = "alpha"
                    )

                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1f else 0.92f,
                        visibilityThreshold = 0.000001f,
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                        ),
                        label = "scale"
                    )

                    Column(
                        modifier = Modifier
                            .scale(scale)
                            .alpha(alpha)
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = screen.iconRes),
                            contentDescription = screen.label,
                            modifier = Modifier.size(28.dp),
                            tint = if (selected)
                                Color(0xFF2196F3)
                            else
                                notSelectedIconColorAnimation.value
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = screen.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (selected)
                                Color(0xFF2196F3)
                            else
                                notSelectedIconColorAnimation.value
                        )
                    }
                }
            }
        }
    }
}
// Add this CompositionLocal to access scale from LiquidBottomTabs
//val LocalLiquidBottomTabScale = compositionLocalOf<() -> Float> { { 1f } }

//@Preview(showBackground = true)
//@Composable
//fun F1CircuitScreenPreview() {
//    F1LiveTheme {
//        // You can create mock data for previews if needed
//        F1CircuitScreen()
//    }
//}