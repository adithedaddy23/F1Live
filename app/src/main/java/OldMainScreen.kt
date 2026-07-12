import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.f1live.bottomnavbar.BottomNavScreen
import com.example.f1live.bottomnavbar.bottomNavItems
import com.example.f1live.repository.Routes
import com.example.f1live.screens.ArchiveScreen
import com.example.f1live.screens.DriverDetailsScreen
import com.example.f1live.screens.F1GrandPrixArchieveScreen
import com.example.f1live.screens.F1GrandPrixScreen
import com.example.f1live.screens.Homescreen
import com.example.f1live.screens.NewsScreen
import com.example.f1live.screens.StandingsScreen
import com.example.f1live.ui.theme.F1LiveTheme
import com.example.f1live.utils.LiquidBottomTabs
import com.example.f1live.widget.F1WidgetUpdateWorker
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import java.util.concurrent.TimeUnit

//package com.example.f1live.utils
//
//@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalSharedTransitionApi::class)
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun MainScreen() {
//    val navController = rememberNavController()
//    val currentBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry?.destination?.route
//
//    // Create HazeState for both status bar and bottom bar blur
//    val hazeState = rememberHazeState()
//
//    // List of routes where bottom bar should be hidden
//    val routesWithoutBottomBar = listOf(
//        Routes.F1GrandPrix.routes,
//        Routes.Driver.routes,
//        Routes.F1GrandPrixArchieve.routes
//    )
//
//    val showBottomBar = routesWithoutBottomBar.none { it == currentRoute }
//    val backdrop = rememberLayerBackdrop()
//    Box(modifier = Modifier.fillMaxSize()) {
//        Scaffold(
//            modifier = Modifier.fillMaxSize(),
//            bottomBar = {
//                if (showBottomBar) {
//                    // Pass the hazeState to BottomNavigationBar
//                    BottomNavigationBar(
//                        navController = navController,
//                        hazeState = hazeState,
//                        backdrop = backdrop
//                    )
//                }
//            }
//        ) { innerPadding ->
//            SharedTransitionLayout {
//                NavHost(
//                    navController = navController,
//                    startDestination = BottomNavScreen.Race.route,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .layerBackdrop(backdrop)
////                    .padding(
////                        bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
////                    )
//                        // Mark content as haze source
//                        .hazeSource(state = hazeState),
//                ) {
//                    composable(BottomNavScreen.Race.route) {
//                        Homescreen(
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
//                    composable(BottomNavScreen.Standings.route) {
//                        StandingsScreen(
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
////                    composable(BottomNavScreen.Drivers.route) {
////                        DriversScreen(
////                            navController = navController,
////                            sharedTransitionScope = this@SharedTransitionLayout,
////                            animatedContentScope = this@composable
////                        )
////                    }
//                    composable(BottomNavScreen.Archive.route) {
//                        ArchiveScreen(navController = navController)
//                    }
//
//                    composable (route = Routes.PathAnimation.routes) {
//                        PathAnimationScreen()
//
//                    }
//
//                    // Grand Prix screen with parameters
//                    composable(
//                        route = Routes.F1GrandPrix.routes,
//                        arguments = listOf(
//                            navArgument("season") { type = NavType.StringType },
//                            navArgument("round") { type = NavType.StringType }
//                        )
//                    ) { backStackEntry ->
//                        val season = backStackEntry.arguments?.getString("season") ?: ""
//                        val round = backStackEntry.arguments?.getString("round") ?: ""
//                        F1GrandPrixScreen(
//                            season = season,
//                            round = round,
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
//
//                    composable(
//                        route = Routes.F1GrandPrix.routes,
//                        arguments = listOf(
//                            navArgument("season") { type = NavType.StringType },
//                            navArgument("round") { type = NavType.StringType }
//                        )
//                    ) { backStackEntry ->
//                        val season = backStackEntry.arguments?.getString("season") ?: ""
//                        val round = backStackEntry.arguments?.getString("round") ?: ""
//                        F1GrandPrixScreen(
//                            season = season,
//                            round = round,
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
//
//                    composable(
//                        route = Routes.F1GrandPrixArchieve.routes,
//                        arguments = listOf(
//                            navArgument("season") { type = NavType.StringType },
//                            navArgument("round") { type = NavType.StringType }
//                        )
//                    ) { backStackEntry ->
//                        val season = backStackEntry.arguments?.getString("season") ?: ""
//                        val round = backStackEntry.arguments?.getString("round") ?: ""
//                        F1GrandPrixArchieveScreen(
//                            season = season,
//                            round = round,
//                            navController = navController,
//                        )
//                    }
//
//                    composable(
//                        route = Routes.Driver.routes,
//                        arguments = listOf(
//                            navArgument("season") { type = NavType.StringType },
//                            navArgument("driverId") { type = NavType.StringType }
//                        )
//                    ) { backStackEntry ->
//                        val season = backStackEntry.arguments?.getString("season") ?: ""
//                        val driverId = backStackEntry.arguments?.getString("driverId") ?: ""
//                        DriverDetailsScreen(
//                            season = season,
//                            driverId = driverId,
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
//
//                    composable(BottomNavScreen.News.route) {
//                        NewsScreen()
//                    }
//                }
//            }
//
//        }
//
//        // Frosted glass status bar overlay
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
//    }
//}
//
//
//@OptIn(ExperimentalHazeMaterialsApi::class)
//@Composable
//fun BottomNavigationBar(
//    navController: NavController,
//    hazeState: HazeState,
//    backdrop: Backdrop
//) {
//    val currentBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination = currentBackStackEntry?.destination
//
//    // Find the index of currently selected tab
//    val selectedTabIndex = bottomNavItems.indexOfFirst {
//        it.route == currentDestination?.route
//    }.takeIf { it != -1 } ?: 0
//
//    // Check if device is running Android 14 (API 34) or higher
//    val isAndroid14OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
//
//    // Adaptive luminance detection
//    val layer = rememberGraphicsLayer()
//    val luminanceAnimation = remember { Animatable(0.5f) }
//    val notSelectedIconColorAnimation = remember { androidx.compose.animation.Animatable(Color(0xFFE0E0E0)) }
//
//    LaunchedEffect(layer) {
//        val buffer = IntBuffer.allocate(25)
//        while (isActive) {
//            withContext(Dispatchers.IO) {
//                val imageBitmap = layer.toImageBitmap()
//                val thumbnail = imageBitmap.asAndroidBitmap()
//                    .scale(5, 5, false)
//                    .copy(Bitmap.Config.ARGB_8888, false)
//                buffer.rewind()
//                thumbnail.copyPixelsToBuffer(buffer)
//            }
//            val averageLuminance = (0 until 25).sumOf { index ->
//                val color = buffer.get(index)
//                val r = (color shr 16 and 0xFF) / 255f
//                val g = (color shr 8 and 0xFF) / 255f
//                val b = (color and 0xFF) / 255f
//                0.2126 * r + 0.7152 * g + 0.0722 * b
//            } / 25
//            launch {
//                notSelectedIconColorAnimation.animateTo(
//                    if (averageLuminance > 0.5f) Color(0xFF13161C) else Color(0xFFE0E0E0),
//                    tween(800)
//                )
//            }
//            luminanceAnimation.animateTo(
//                averageLuminance.toFloat(),
//                tween(400)
//            )
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(bottom = 26.dp, top = 16.dp, start = 38.dp, end = 38.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(60.dp)
//                .shadow(
//                    elevation = 12.dp,
//                    shape = ContinuousCapsule,
//                    ambientColor = Color.White.copy(alpha = 0.15f),
//                    spotColor = Color.White.copy(alpha = 0.25f)
//                )
//                .then(
//                    if (isAndroid14OrAbove) {
//                        Modifier.drawBackdrop(
//                            backdrop = backdrop,
//                            shape = { ContinuousCapsule },
//                            effects = {
//                                // Adaptive effects based on luminance
//                                val l = (luminanceAnimation.value * 2f - 1f).let {
//                                    sign(it) * it * it
//                                }
////                                vibrancy()
//                                colorControls(
//                                    brightness = if (l > 0f) lerp(0.1f, 0.5f, l)
//                                    else lerp(0.1f, -0.2f, -l),
//                                    contrast = if (l > 0f) lerp(1f, 0f, l)
//                                    else 1f,
//                                    saturation = 1.3f,
//                                )
//
//                                blur(
//                                    if (l > 0f) lerp(3f.dp.toPx(), 2.5f.dp.toPx(), l)
//                                    else lerp(3f.dp.toPx(), 2.5f.dp.toPx(), -l),
//                                    edgeTreatment = TileMode.Decal
//                                )
//
//                                lens(
//                                    20f.dp.toPx(),
//                                    38f.dp.toPx(),
//                                    chromaticAberration = false,
//                                    depthEffect = false
//                                )
//                            },
//                            onDrawBackdrop = { drawBackdrop ->
//                                drawBackdrop()
//                                layer.record { drawBackdrop() }
//                            }
//                        )
//                    } else {
//                        Modifier.background(
//                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
//                            ContinuousCapsule
//                        )
//                    }
//                )
//                .clip(RoundedCornerShape(32.dp))
//        ) {
//            // Bottom navigation items
//            Row(
//                modifier = Modifier.fillMaxSize(),
//                horizontalArrangement = Arrangement.SpaceEvenly,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                bottomNavItems.forEachIndexed { index, screen ->
//                    val selected = currentDestination?.route == screen.route
//
//                    val alpha by animateFloatAsState(
//                        targetValue = if (selected) 1f else 0.8f,
//                        label = "alpha"
//                    )
//
//                    val scale by animateFloatAsState(
//                        targetValue = if (selected) 1f else 0.92f,
//                        visibilityThreshold = 0.000001f,
//                        animationSpec = spring(
//                            stiffness = Spring.StiffnessLow,
//                            dampingRatio = Spring.DampingRatioMediumBouncy,
//                        ),
//                        label = "scale"
//                    )
//
//                    Column(
//                        modifier = Modifier
//                            .scale(scale)
//                            .alpha(alpha)
//                            .weight(1f)
//                            .fillMaxHeight()
//                            .clickable(
//                                onClick = {
//                                    navController.navigate(screen.route) {
//                                        popUpTo(navController.graph.startDestinationId) {
//                                            saveState = true
//                                        }
//                                        launchSingleTop = true
//                                        restoreState = true
//                                    }
//                                },
//                                indication = null,
//                                interactionSource = remember { MutableInteractionSource() }
//                            ),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Icon(
//                            painter = painterResource(id = screen.iconRes),
//                            contentDescription = screen.label,
//                            modifier = Modifier.size(28.dp),
//                            tint =  if (selected)
//                                Color(0xFF2196F3)
//                            else
//                                notSelectedIconColorAnimation.value
//                        )
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Text(
//                            text = screen.label,
//                            style = MaterialTheme.typography.labelMedium,
//                            fontWeight = FontWeight.Bold,
//                            color = if (selected)
//                                Color(0xFF2196F3)
//                            else
//                                notSelectedIconColorAnimation.value
//                        )
//                    }
//                }
//            }
//        }
//    }
//}


// <----------------------------------------------- New Main Screen -----------------------------------------------------> //

//class MainActivity : ComponentActivity() {
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val updateRequest = PeriodicWorkRequestBuilder<F1WidgetUpdateWorker>(
//            6, TimeUnit.HOURS // Update every 6 hours
//        )
//            .setInitialDelay(0, TimeUnit.SECONDS) // Update immediately on first launch
//            .build()
//
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//            "F1WidgetUpdate",
//            ExistingPeriodicWorkPolicy.KEEP,
//            updateRequest
//        )
//        enableEdgeToEdge(
//            statusBarStyle = SystemBarStyle.dark(
//                scrim = Color.Transparent.toArgb()
//            )
//        )
//        setContent {
//            F1LiveTheme {
//                MainScreen()
//            }
//        }
//    }
//}
//
//
//
//@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalSharedTransitionApi::class)
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun MainScreen() {
//    val navController = rememberNavController()
//    val currentBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry?.destination?.route
//
//    // Create HazeState for both status bar and bottom bar blur
//    val hazeState = rememberHazeState()
//
//    // List of routes where bottom bar should be hidden
//    val routesWithoutBottomBar = listOf(
//        Routes.F1GrandPrix.routes,
//        Routes.Driver.routes,
//        Routes.F1GrandPrixArchieve.routes
//    )
//
//    val showBottomBar = routesWithoutBottomBar.none { it == currentRoute }
//    val backdrop = rememberLayerBackdrop()
//    Box(modifier = Modifier.fillMaxSize()) {
//        Scaffold(
//            modifier = Modifier.fillMaxSize(),
//            bottomBar = {
//                if (showBottomBar) {
//                    // Pass the hazeState to BottomNavigationBar
//                    BottomNavigationBar(
//                        navController = navController,
//                        backdrop = backdrop
//                    )
//                }
//            }
//        ) { innerPadding ->
//            SharedTransitionLayout {
//                NavHost(
//                    navController = navController,
//                    startDestination = BottomNavScreen.Race.route,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .layerBackdrop(backdrop)
////                    .padding(
////                        bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
////                    )
//                        // Mark content as haze source
//                        .hazeSource(state = hazeState),
//                ) {
//                    composable(BottomNavScreen.Race.route) {
//                        Homescreen(
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
//                    composable(BottomNavScreen.Standings.route) {
//                        StandingsScreen(
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
////                    composable(BottomNavScreen.Drivers.route) {
////                        DriversScreen(
////                            navController = navController,
////                            sharedTransitionScope = this@SharedTransitionLayout,
////                            animatedContentScope = this@composable
////                        )
////                    }
//                    composable(BottomNavScreen.Archive.route) {
//                        ArchiveScreen(navController = navController)
//                    }
//
//                    composable (route = Routes.PathAnimation.routes) {
//                        PathAnimationScreen()
//
//                    }
//
//                    // Grand Prix screen with parameters
//                    composable(
//                        route = Routes.F1GrandPrix.routes,
//                        arguments = listOf(
//                            navArgument("season") { type = NavType.StringType },
//                            navArgument("round") { type = NavType.StringType }
//                        )
//                    ) { backStackEntry ->
//                        val season = backStackEntry.arguments?.getString("season") ?: ""
//                        val round = backStackEntry.arguments?.getString("round") ?: ""
//                        F1GrandPrixScreen(
//                            season = season,
//                            round = round,
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
//
//                    composable(
//                        route = Routes.F1GrandPrix.routes,
//                        arguments = listOf(
//                            navArgument("season") { type = NavType.StringType },
//                            navArgument("round") { type = NavType.StringType }
//                        )
//                    ) { backStackEntry ->
//                        val season = backStackEntry.arguments?.getString("season") ?: ""
//                        val round = backStackEntry.arguments?.getString("round") ?: ""
//                        F1GrandPrixScreen(
//                            season = season,
//                            round = round,
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
//
//                    composable(
//                        route = Routes.F1GrandPrixArchieve.routes,
//                        arguments = listOf(
//                            navArgument("season") { type = NavType.StringType },
//                            navArgument("round") { type = NavType.StringType }
//                        )
//                    ) { backStackEntry ->
//                        val season = backStackEntry.arguments?.getString("season") ?: ""
//                        val round = backStackEntry.arguments?.getString("round") ?: ""
//                        F1GrandPrixArchieveScreen(
//                            season = season,
//                            round = round,
//                            navController = navController,
//                        )
//                    }
//
//                    composable(
//                        route = Routes.Driver.routes,
//                        arguments = listOf(
//                            navArgument("season") { type = NavType.StringType },
//                            navArgument("driverId") { type = NavType.StringType }
//                        )
//                    ) { backStackEntry ->
//                        val season = backStackEntry.arguments?.getString("season") ?: ""
//                        val driverId = backStackEntry.arguments?.getString("driverId") ?: ""
//                        DriverDetailsScreen(
//                            season = season,
//                            driverId = driverId,
//                            navController = navController,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedContentScope = this@composable
//                        )
//                    }
//
//                    composable(BottomNavScreen.News.route) {
//                        NewsScreen()
//                    }
//                }
//            }
//
//        }
//
//        // Frosted glass status bar overlay
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
//    }
//}
//
//
//@Composable
//fun BottomNavigationBar(
//    navController: NavController,
//    backdrop: Backdrop
//) {
//    val currentBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination = currentBackStackEntry?.destination
//
//    // Find the index of currently selected tab
//    var selectedTabIndex by remember { mutableIntStateOf(0) }
//
//    // Update selectedTabIndex when route changes
//    LaunchedEffect(currentDestination?.route) {
//        val index = bottomNavItems.indexOfFirst {
//            it.route == currentDestination?.route
//        }.takeIf { it != -1 } ?: 0
//        if (selectedTabIndex != index) {
//            selectedTabIndex = index
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(bottom = 26.dp, top = 16.dp, start = 38.dp, end = 38.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        LiquidBottomTabs(
//            selectedTabIndex = { selectedTabIndex },
//            onTabSelected = { index ->
//                selectedTabIndex = index
//                val screen = bottomNavItems[index]
//                navController.navigate(screen.route) {
//                    popUpTo(navController.graph.startDestinationId) {
//                        saveState = true
//                    }
//                    launchSingleTop = true
//                    restoreState = true
//                }
//            },
//            backdrop = backdrop,
//            tabsCount = bottomNavItems.size,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            bottomNavItems.forEachIndexed { index, screen ->
//                // Use the LiquidBottomTab wrapper - it handles clicks internally
//                LiquidBottomTab(onClick = { selectedTabIndex = index }) {
//                    TabContent(
//                        screen = screen,
//                        selected = selectedTabIndex == index
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun TabContent(
//    screen: BottomNavScreen,
//    selected: Boolean
//) {
//    // Get the scale from LocalLiquidBottomTabScale
//    val scale = LocalLiquidBottomTabScale.current.invoke()
//
//    Column(
//        modifier = Modifier
//            .fillMaxHeight()
//            .graphicsLayer {
//                scaleX = if (selected) scale else 1f
//                scaleY = if (selected) scale else 1f
//            },
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Icon(
//            painter = painterResource(id = screen.iconRes),
//            contentDescription = screen.label,
//            modifier = Modifier.size(24.dp),
//            tint = if (selected)
//                Color(0xFF2196F3)
//            else
//                Color(0xFFE0E0E0)
//        )
//
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = screen.label,
//                style = MaterialTheme.typography.labelSmall,
//                fontWeight = FontWeight.Bold,
//                fontSize = 10.sp,
//                color = if (selected)
//                    Color(0xFF2196F3)
//                else
//                    Color(0xFFE0E0E0)
//            )
//        }
//
//    }
//}
//
//// IMPORTANT: Add this CompositionLocal definition at the top level of your file
//val LocalLiquidBottomTabScale = compositionLocalOf<() -> Float> { { 1f } }
//
//// ADD THIS: The LiquidBottomTab wrapper function that handles clicks
//@Composable
//fun RowScope.LiquidBottomTab(
//    onClick: () -> Unit,
//    content: @Composable ColumnScope.() -> Unit
//) {
//    val interactionSource = remember { MutableInteractionSource() }
//
//    Column(
//        modifier = Modifier
//            .weight(1f)
//            .fillMaxHeight()
//            .clickable(
//                onClick = onClick,
//                indication = null,
//                interactionSource = interactionSource
//            ),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        content = content
//    )
//}