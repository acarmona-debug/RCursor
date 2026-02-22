package com.actividadapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Teal200 = Color(0xFF03DAC6)
private val Teal700 = Color(0xFF018786)
private val Violet = Color(0xFF6750A4)
private val VioletDark = Color(0xFF3700B3)

private val DarkColorScheme = darkColorScheme(
    primary = Teal200,
    secondary = Teal700,
    tertiary = Violet
)

private val LightColorScheme = lightColorScheme(
    primary = Violet,
    secondary = Teal700,
    tertiary = VioletDark
)

@Composable
fun ActividadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
