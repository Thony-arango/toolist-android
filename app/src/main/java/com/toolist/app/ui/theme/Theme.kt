package com.toolist.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ---------------------------------------------------------------------------
// Color schemes — extraídas de CONTEXT.md
// ---------------------------------------------------------------------------

private val LightColorScheme = lightColorScheme(
    primary = Green500,
    onPrimary = White,
    primaryContainer = Green100,
    onPrimaryContainer = Green700,
    secondary = Green700,
    onSecondary = White,
    secondaryContainer = Green50,
    onSecondaryContainer = Green900,
    background = Gray50,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray600,
    outline = Gray200,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRedDark,
)

private val DarkColorScheme = darkColorScheme(
    primary = Green300,
    onPrimary = Green900,
    primaryContainer = Green700,
    onPrimaryContainer = Green100,
    secondary = Green200,
    onSecondary = Green900,
    secondaryContainer = Green700,
    onSecondaryContainer = Green100,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Gray400,
    outline = DarkOutline,
    error = ErrorRedDarkMode,
    onError = ErrorRedDarkModeOn,
    errorContainer = ErrorRedContainerDark,
    onErrorContainer = ErrorRedLight,
)

// ---------------------------------------------------------------------------
// Tema principal
// ---------------------------------------------------------------------------

@Composable
fun ToolistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
