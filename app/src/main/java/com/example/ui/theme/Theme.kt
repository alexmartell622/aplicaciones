package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FarmaBluePrimaryDark,
    secondary = FarmaTealSecondaryDark,
    background = FarmaBackgroundDark,
    surface = FarmaSurfaceDark,
    onPrimary = FarmaBackgroundDark,
    onSecondary = FarmaBackgroundDark
)

private val LightColorScheme = lightColorScheme(
    primary = FarmaBluePrimary,
    secondary = FarmaTealSecondary,
    background = FarmaBackgroundLight,
    surface = FarmaSurfaceLight,
    onPrimary = FarmaSurfaceLight,
    onSecondary = FarmaSurfaceLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep consistent clean medical theme first by default
    dynamicColor: Boolean = false,
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
