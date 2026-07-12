package com.example.f1live.screens

import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.f1live.api.Driver
import com.example.f1live.api.Drivers
import com.example.f1live.api.UiState
import com.example.f1live.repository.DriversImg
import com.example.f1live.repository.Routes
import com.example.f1live.viewmodel.F1ViewModel
import java.time.LocalDate
import kotlin.math.absoluteValue

// 1. Add this new import for HorizontalPager
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi // And this one
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.zIndex
import com.example.f1live.repository.DriverDImg
import kotlin.math.abs

// ... other imports ...

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)

@RequiresApi(Build.VERSION_CODES.O)

@Composable

fun DriversScreen(

    viewModel: F1ViewModel = viewModel(),

    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,

) {


    val driverState by viewModel.driversState.collectAsState()
    val year = LocalDate.now().year


// Get padding for BOTH top and bottom system bars
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
// val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomNavPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    var isInitialLoad by remember { mutableStateOf(true) }

    LaunchedEffect(driverState) {
        viewModel.fetchDrivers(year.toString())

    }



    LazyColumn(

        modifier = Modifier.fillMaxSize(),

        verticalArrangement = Arrangement.spacedBy(10.dp), // <-- FIX 1: Set spacing here

        contentPadding = PaddingValues(

            start = 16.dp,

            end = 16.dp,

            top = 24.dp + statusBarPadding,

            bottom = 24.dp + bottomNavPadding + 80.dp

        )

    ) {

        item {

            Text(

                text = "Drivers $year",

                style = MaterialTheme.typography.headlineMedium,

                fontWeight = FontWeight.Bold,

                color = MaterialTheme.colorScheme.primary

// <-- FIX 2: Removed manual padding from here

            )

        }



        when (val state = driverState) {

            is UiState.Loading -> {

                if (isInitialLoad) {

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

                } else {

// You might want a different loading state for refreshes

                }

            }


            is UiState.Success -> {

                isInitialLoad = false // Data has loaded

                val drivers = state.data.MRData.DriverTable.Drivers



                items(drivers, key = { it.driverId }) { driver ->

                    DriverCard(
                        driver = driver,
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope
                    )

// <-- FIX 3: Removed the manual Spacer(..) from here

                }

            }

            is UiState.Error -> {

// ... (error state unchanged)

            }

        }

    }
}



/**
 * Helper function to map a driverId to a constructorId for 2025.
 * This is needed to fetch the correct team colors.
 */
fun getConstructorIdForDriver(driverId: String): String {
    return when (driverId) {
        "piastri", "norris" -> "mclaren"
        "russell", "antonelli" -> "mercedes"
        "leclerc", "hamilton" -> "ferrari"
        // Based on your DriversImg URLs, Tsunoda is at Red Bull
        "max_verstappen", "hadjar" -> "red_bull"
        "albon", "sainz" -> "williams"
        "lindblad", "lawson" -> "rb"
        "stroll", "alonso" -> "aston_martin"
        "hülkenberg ", "bortoleto" -> "audi"
        "ocon", "bearman" -> "haas"
        "gasly", "colapinto", "doohan" -> "alpine"
        "bottas","perez" -> "cadillac"

        else -> "default" // Fallback for unknown drivers
    }
}

/**
 * Helper function to get the driver's image URL from the DriversImg object.
 */
fun getDriverImageUrl(driverFullName: String): String {
    // Default fallback image in case a driver isn't found
    val fallbackImage = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2025:fallback:driver:2025fallbackdriverright.webp/v1740000000/common/f1/2025/fallback/2025fallbackdriver.webp"

    return DriversImg.drivers.find { it.name.equals(driverFullName, ignoreCase = true) }?.imgUrl
        ?: fallbackImage
}

@RequiresApi(Build.VERSION_CODES.O)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)

@Composable

fun DriverCard(

    driver: com.example.f1live.api.Driver,

    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,

// teamName: String // Add teamName as a parameter

) {

// 1. Get Data for the Card

    val constructorId = getConstructorIdForDriver(driver.driverId)

    val teamColorInfo = getTeamColorInfo(constructorId)

    val driverFullName = "${driver.givenName} ${driver.familyName}"

    val driverImageUrl = getDriverImageUrl(driverFullName)

    val year = LocalDate.now().year

    val driverId = driver.driverId



// Helper to get flag URL (you might need a more robust solution)

//    val flagUrl = "https://flagsapi.com/${getTwoLetterCountryCode(driver.nationality)}/flat/64.png"



    Card(

        modifier = Modifier

            .fillMaxWidth()

            .clickable{

                navController.navigate(Routes.Driver.createRoute2(year.toString(),driverId))

            },

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

                verticalAlignment = Alignment.CenterVertically,

                ) {

// 1. Permanent Number

                Box(

                    modifier = Modifier.width(45.dp),

                    contentAlignment = Alignment.Center

                ) {

                    Text(

                        text = driver.permanentNumber,

                        style = MaterialTheme.typography.titleLarge,

                        fontWeight = FontWeight.Black,

                        color = Color.White,

                        textAlign = TextAlign.Center

                    )

                }



                Spacer(modifier = Modifier.width(8.dp))



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

                            .data(driverImageUrl)

                            .crossfade(300)

                            .memoryCachePolicy(CachePolicy.ENABLED)

                            .diskCachePolicy(CachePolicy.ENABLED)

                            .build(),

                        contentDescription = driverFullName,

                        modifier = Modifier

                            .height(90.dp)

                            .fillMaxWidth(),

                        contentScale = ContentScale.Crop,

                        alignment = Alignment.TopCenter

                    )

                }



                Spacer(modifier = Modifier.width(16.dp))



// 3. Driver and Team Info

                Column(

                    modifier = Modifier.weight(0.7f),

                    verticalArrangement = Arrangement.Center

                ) {

// Driver Name
                    with(sharedTransitionScope) {
                        Text(
                            text = driver.givenName,
                            style = MaterialTheme.typography.titleSmallEmphasized,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
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
                            text = driver.familyName.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.sharedBounds(
                                sharedTransitionScope.rememberSharedContentState(key = "race-name-${driver.familyName}"),
                                animatedVisibilityScope = animatedContentScope,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        ) )
                    }




                    Spacer(modifier = Modifier.height(4.dp))



// // Team Logo and Name

// Row(

// verticalAlignment = Alignment.CenterVertically

// ) {

// AsyncImage(

// model = teamLogo,

// contentDescription = "$constructorName Logo",

// modifier = Modifier

// .height(16.dp)

// .padding(end = 6.dp),

// contentScale = ContentScale.Fit

// )

// Text(

// text = constructorName,

// fontSize = 12.sp,

// color = Color.White.copy(alpha = 0.6f),

// fontWeight = FontWeight.Medium

// )

// }

                }



                Spacer(modifier = Modifier.width(12.dp))



// 4. Nationality

                Box(

                    modifier = Modifier.width(65.dp),

                    contentAlignment = Alignment.Center

                ) {

                    Column(

                        horizontalAlignment = Alignment.CenterHorizontally,

                        verticalArrangement = Arrangement.Center

                    ) {

                        Text(

                            text = driver.nationality,

                            style = MaterialTheme.typography.labelMedium,

                            fontWeight = FontWeight.Black,

                            color = Color.White,

                            overflow = TextOverflow.Ellipsis

                        )

// Text(

// text = "PTS",

// fontSize = 10.sp,

// color = Color.White.copy(alpha = 0.5f),

// fontWeight = FontWeight.Bold,

// letterSpacing = 1.sp

// )

                    }

                }

            }

        }

    }

}

// Helper to get a two-letter country code for the flag API
fun getTwoLetterCountryCode(nationality: String): String {

    if(nationality == null) {
        return "UN"
    }

    return when (nationality.lowercase()) {
        "british" -> "gb" // United Kingdom
        "spanish" -> "es"
        "dutch" -> "nl"
        "mexican" -> "mx"
        "thai" -> "th" // For Albon (Thai-British, often represented with TH)
        "french" -> "fr"
        "australian" -> "au"
        "monacan" -> "mc"
        "german" -> "de"
        "canadian" -> "ca"
        "finnish" -> "fi"
        "japanese" -> "jp"
        "danish" -> "dk"
        "chinese" -> "cn"
        "american" -> "us"
        "argentine" -> "ar" // For Colapinto
        "italian" -> "it" // For Antonelli
        // Add more as needed
        else -> "gb" // Default to GB or a generic flag
    }
}