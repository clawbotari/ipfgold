package com.ipfgold.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Esquema de colores claro (light)
private val LightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.White,
    primaryContainer = GoldSecondary,
    onPrimaryContainer = Color.White,
    secondary = GoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = GoldSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = GoldSecondary,
    tertiary = BullishGreen,
    onTertiary = Color.White,
    tertiaryContainer = BullishGreen.copy(alpha = 0.2f),
    onTertiaryContainer = BullishGreen,
    background = Gray10,
    onBackground = Gray90,
    surface = SurfaceLight,
    onSurface = Gray90,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Gray70,
    outline = Gray40,
    outlineVariant = Gray30,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
)

// Esquema de colores oscuro (dark)
private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = GoldSecondary,
    onPrimaryContainer = Color.Black,
    secondary = GoldSecondary,
    onSecondary = Color.Black,
    secondaryContainer = GoldSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = GoldSecondary,
    tertiary = BullishGreen,
    onTertiary = Color.Black,
    tertiaryContainer = BullishGreen.copy(alpha = 0.2f),
    onTertiaryContainer = BullishGreen,
    background = Gray90,
    onBackground = Gray10,
    surface = SurfaceDark,
    onSurface = Gray10,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Gray40,
    outline = Gray60,
    outlineVariant = Gray70,
    error = ErrorRed,
    onError = Color.Black,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
)

// Proveedor de colores personalizados (para acceder a colores semánticos extras)
private val LocalCustomColors = staticCompositionLocalOf { LightColorScheme }

/**
 * Tema principal de la aplicación IPF Gold.
 *
 * Soporta light/dark según la preferencia del sistema.
 * Incluye paleta dorada financiera y colores semánticos para tendencias.
 */
@Composable
fun IpfGoldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalCustomColors provides colorScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Extensión para acceder a colores semánticos adicionales desde el tema.
 */
val MaterialTheme.customColors: ColorScheme
    @Composable
    get() = LocalCustomColors.current