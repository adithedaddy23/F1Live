package com.example.f1live.widget
import androidx.glance.GlanceModifier
import androidx.glance.layout.Box
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontFamily
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.f1live.MainActivity
import com.example.f1live.R
import com.example.f1live.repository.TrackPhotos

@RequiresApi(Build.VERSION_CODES.O)
class F1GrandPrixWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                F1WidgetContent(context)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("RestrictedApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun F1WidgetContent(context: Context) {
    var raceInfo by remember { mutableStateOf<F1WidgetRaceInfo?>(null) }
    var trackImageBitmap by remember { mutableStateOf<Bitmap?>(null) }


    LaunchedEffect(Unit) {
        raceInfo = F1WidgetDataManager.getRaceData(context)
        // Fetch track image URL based on race name
        raceInfo?.let { race ->
            val url = TrackPhotos.getTrackList()
                .find { it.gpName == race.raceName }
                ?.imgUrl

            if (url != null) {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false) // IMPORTANT: Widgets crash with Hardware Bitmaps
                    .build()

                val result = loader.execute(request)
                if (result is SuccessResult) {
                    trackImageBitmap = result.drawable.toBitmap()
                }
            }
        }
    }

    // Create intent to open app
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color.Transparent))
            .clickable(actionStartActivity(intent)),
        contentAlignment = Alignment.Center
    ) {
        // Background image from URL
        if (trackImageBitmap != null) {
            Image(
                provider = ImageProvider(trackImageBitmap!!), // Pass Bitmap here
                contentDescription = "Track Background",
                modifier = GlanceModifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback background
            Image(
                provider = ImageProvider(R.drawable._025_japanese_gp___race_start_2),
                contentDescription = "Track Background",
                modifier = GlanceModifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Dark overlay
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xAA000000))),
            content = {}
        )

        // Content
        if (raceInfo != null) {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                // Header Section
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Horizontal.Start
                ) {
                    Text(
                        text = if (raceInfo!!.isOngoing) "RACE WEEKEND" else "NEXT RACE",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(
                                if (raceInfo!!.isOngoing)
                                    Color(0xFF2196F3)
                                else
                                    Color.White
                            )
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    Text(
                        text = raceInfo!!.raceName,
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(androidx.compose.ui.graphics.Color.White)
                        )
                    )

//                    Spacer(modifier = GlanceModifier.height(4.dp))
//
//                    Text(
//                        text = raceInfo!!.circuitName,
//                        style = TextStyle(
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = ColorProvider(androidx.compose.ui.graphics.Color(0xCCFFFFFF))
//                        )
//                    )

                    Text(
                        text = raceInfo!!.location,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = ColorProvider(androidx.compose.ui.graphics.Color(0xAAFFFFFF))
                        )
                    )
                }

                // Bottom Section
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Horizontal.Start
                ) {
                    Text(
                        text = raceInfo!!.formattedDate,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(androidx.compose.ui.graphics.Color.White)
                        )
                    )

                    if (raceInfo!!.formattedTime != null) {
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = raceInfo!!.formattedTime!!,
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF2196F3))
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(4.dp))

                    if (raceInfo!!.isOngoing) {
                        Text(
                            text = raceInfo!!.ongoingStatus,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF4CAF50))
                            )
                        )
                    } else {
                        Text(
                            text = raceInfo!!.daysUntil,
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = ColorProvider(androidx.compose.ui.graphics.Color(0xAAFFFFFF))
                            )
                        )
                    }
                }
            }
        } else {
            // No race data
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text(
                    text = "🏎️",
                    style = TextStyle(fontSize = 48.sp)
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Text(
                    text = "Loading F1 data...",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White)
                    )
                )
            }
        }
    }
}

class F1GrandPrixWidgetReceiver : GlanceAppWidgetReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override val glanceAppWidget: GlanceAppWidget = F1GrandPrixWidget()
}