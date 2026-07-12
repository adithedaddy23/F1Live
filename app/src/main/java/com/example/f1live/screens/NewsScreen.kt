package com.example.f1live.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.packInts
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.f1live.R
import com.example.f1live.api.UiState
import com.example.f1live.news.NewsArticle
import com.example.f1live.news.RssFeed
import com.example.f1live.viewmodel.F1ViewModel
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsScreen(
    viewModel: F1ViewModel = viewModel()
) {
    val newsState by viewModel.newsState.collectAsState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    LaunchedEffect(Unit) {
        viewModel.fetchNews()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp, vertical = 16.dp)
//        ) {

        }

        when (val state = newsState) {
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

            is UiState.Success -> {
                // FIXED: The data is already List<NewsArticle>, no need to cast to RssFeed
                val articles = state.data as? List<NewsArticle> ?: emptyList()

                if (articles.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No news found", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            end = 12.dp,
                            top = statusBarPadding,
                            bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "F1 News",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(articles) { article ->
                            NewsCardModern(article = article)
                        }
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No News Found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchNews(forceRefresh = true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }

}


// ============================================
// FIXED NewsCard - Re-enable the clickable link
// ============================================
@OptIn(ExperimentalHazeMaterialsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsCardModern(
    article: NewsArticle,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val hazeState = rememberHazeState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp) // Outer spacing
            .clip(RoundedCornerShape(24.dp)) // Softer, larger corners
            .clickable {
                if (article.link.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.link.trim()))
                    context.startActivity(intent)
                }
            }
            ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer // Slightly distinct from background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat is more modern
    ) {
        Column {
            // 1. Image Area (Fixed Aspect Ratio)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val imageUrl = article.enclosure?.url
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(800)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeSource(state = hazeState),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(
                        onClick = {
                            if (article.link.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.link.trim()))
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
//                            .background(color = Color.Black, shape = CircleShape)
                            .hazeEffect(
                                state = hazeState,
                                style = CupertinoMaterials.thin()
                            ) {
                                blurRadius = 20.dp
                            },
//                        colors = IconButtonDefaults.iconButtonColors(
//                            containerColor =Color(0xFF2196F3).copy(0.7f),
//                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_outward_24dp_000000_fill0_wght400_grad0_opsz24),
                            contentDescription = "Read article",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFFFFFFFF)
                        )
                    }
//                    Button(
//                        onClick = {},
//                        modifier = Modifier.padding(8.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.primary.copy(0.8f),
//                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                        ),
//                        shape = CircleShape
//                    ) {
////                        Text(
////                            text = "Read More",
////                            fontSize = 12.sp
////                        )
//                        Icon(
//                            painter = painterResource(R.drawable.arrow_outward_24dp_000000_fill0_wght400_grad0_opsz24),
//                            contentDescription = "Read article",
//                            modifier = Modifier.size(20.dp),
//                            tint = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
                }

            }

            // 2. Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Metadata Row (Date & Read time/Icon)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatPubDate(article.pubDate).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // Minimal Arrow Icon
//                    Icon(
//                        painter = painterResource(R.drawable.arrow_outward_24dp_000000_fill0_wght400_grad0_opsz24),
//                        contentDescription = "Read article",
//                        modifier = Modifier.size(20.dp),
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = article.title.trim(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        lineHeight = 28.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description (Lighter)
                val rawDescription = article.description
                if (rawDescription.isNotBlank()) {
                    Text(
                        text = stripHtml(rawDescription),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// --- HELPER FUNCTION ---
// Removes HTML tags from RSS descriptions (e.g., <p>Text</p> -> Text)
fun stripHtml(html: String): String {
    return android.text.Html.fromHtml(html, android.text.Html.FROM_HTML_MODE_LEGACY).toString().trim()
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatPubDate(pubDate: String): String {
    return try {
        val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
        val dateTime = ZonedDateTime.parse(pubDate, formatter)
        val now = ZonedDateTime.now()

        val days = ChronoUnit.DAYS.between(dateTime, now)

        when {
            days == 0L -> {
                val hours = ChronoUnit.HOURS.between(dateTime, now)
                if (hours == 0L) {
                    val minutes = ChronoUnit.MINUTES.between(dateTime, now)
                    "$minutes minutes ago"
                } else {
                    "$hours hours ago"
                }
            }
            days == 1L -> "Yesterday"
            days < 7 -> "$days days ago"
            else -> dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        }
    } catch (e: Exception) {
        pubDate
    }
}