package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = NavyDark,
    onPrimaryContainer = TealLight,
    secondary = AmberGold,
    onSecondary = Color.White,
    secondaryContainer = AmberGoldDark,
    onSecondaryContainer = AmberGoldLight,
    tertiary = BlueAccent,
    onTertiary = Color.White,
    background = Slate900,
    onBackground = Slate50,
    surface = Slate800,
    onSurface = Slate50,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate200,
    outline = Slate500
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Slate100,
    onPrimaryContainer = NavyDark,
    secondary = TealPrimary,
    onSecondary = Color.White,
    secondaryContainer = TealLight,
    onSecondaryContainer = TealDark,
    tertiary = AmberGold,
    onTertiary = Color.White,
    tertiaryContainer = AmberGoldLight,
    onTertiaryContainer = AmberGoldDark,
    background = BackgroundLight,
    onBackground = Slate900,
    surface = SurfaceLight,
    onSurface = Slate900,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Slate700,
    outline = CardBorder
)

@Composable
fun ArriendoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our polished brand palette
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    ArriendoTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
