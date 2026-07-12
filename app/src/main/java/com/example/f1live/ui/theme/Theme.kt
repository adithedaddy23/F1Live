package com.example.f1live.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val F1Red = Color(0xFFE10600)
val CarbonBlack = Color(0xFF000000)
val AsphaltGrey = Color(0xFF1E1E1E)
val PodiumGold = Color(0xFFFFD700)
val SilverTrophy = Color(0xFFB0B3B8)
val CleanWhite = Color(0xFFFFFFFF)
val LightGrey = Color(0xFFF2F2F7)

// Dark Theme Colors
val primaryDark = F1Red
val onPrimaryDark = CleanWhite
val primaryContainerDark = Color(0xFFB00000)
val onPrimaryContainerDark = Color(0xFFFFDAD4)
val secondaryDark = SilverTrophy
val onSecondaryDark = CarbonBlack
val secondaryContainerDark = Color(0xFF333639)
val onSecondaryContainerDark = Color(0xFFDDE1E4)
val tertiaryDark = PodiumGold
val onTertiaryDark = CarbonBlack
val tertiaryContainerDark = Color(0xFF4D4000)
val onTertiaryContainerDark = Color(0xFFFFDEBC)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = CarbonBlack
val onBackgroundDark = Color(0xFFE0E0E0)
val surfaceDark = AsphaltGrey
val onSurfaceDark = Color(0xFFE0E0E0)
val surfaceVariantDark = Color(0xFF45464F)
val onSurfaceVariantDark = Color(0xFFC5C6D0)
val outlineDark = Color(0xFF8F909A)

// Light Theme Colors
val primaryLight = F1Red
val onPrimaryLight = CleanWhite
val primaryContainerLight = Color(0xFFFFDAD4)
val onPrimaryContainerLight = Color(0xFF410001)
val secondaryLight = Color(0xFF535F70)
val onSecondaryLight = CleanWhite
val secondaryContainerLight = Color(0xFFD6E4F7)
val onSecondaryContainerLight = Color(0xFF101C2B)
val tertiaryLight = Color(0xFF6B5778)
val onTertiaryLight = CleanWhite
val tertiaryContainerLight = Color(0xFFF3DAFF)
val onTertiaryContainerLight = Color(0xFF251431)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = CleanWhite
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = LightGrey
val onBackgroundLight = CarbonBlack
val surfaceLight = CleanWhite
val onSurfaceLight = CarbonBlack
val surfaceVariantLight = Color(0xFFE0E2EC)
val onSurfaceVariantLight = Color(0xFF43474E)
val outlineLight = Color(0xFF74777F)

val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    inverseOnSurface = onBackgroundLight,
    inverseSurface = backgroundLight,
    inversePrimary = primaryLight,
    surfaceTint = primaryDark,
)

val LightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    inverseOnSurface = onBackgroundDark,
    inverseSurface = backgroundDark,
    inversePrimary = primaryDark,
    surfaceTint = primaryLight,
)

@Composable
fun F1LiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}