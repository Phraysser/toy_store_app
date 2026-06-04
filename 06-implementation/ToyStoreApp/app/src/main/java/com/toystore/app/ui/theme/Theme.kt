package com.toystore.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

//  ИГРУШЕЧНАЯ ЦВЕТОВАЯ СХЕМА
private val ToyPrimary = Color(0xFFFF6B6B)           // Яркий коралловый
private val ToyPrimaryDark = Color(0xFFFF8E8E)
private val ToySecondary = Color(0xFF4ECDC4)         // Бирюзовый
private val ToySecondaryDark = Color(0xFF6EE7DE)
private val ToyAccent = Color(0xFFFFD93D)             // Солнечный жёлтый
private val ToyBackground = Color(0xFFFFF9F0)         // Тёплый кремовый
private val ToyBackgroundDark = Color(0xFF1A1A2E)     // Тёмный индиго
private val ToySurface = Color(0xFFFFFFFF)
private val ToySurfaceDark = Color(0xFF16213E)
private val ToyOnBackground = Color(0xFF2D3436)
private val ToyOnBackgroundDark = Color(0xFFF7F9FC)

// Светлая тема
private val LightColorScheme = lightColorScheme(
    primary = ToyPrimary,
    onPrimary = Color.White,
    primaryContainer = ToyPrimary.copy(alpha = 0.1f),
    secondary = ToySecondary,
    onSecondary = Color.White,
    tertiary = ToyAccent,
    onTertiary = Color.Black,
    background = ToyBackground,
    onBackground = ToyOnBackground,
    surface = ToySurface,
    onSurface = ToyOnBackground,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = ToyOnBackground.copy(alpha = 0.7f),
    error = Color(0xFFE74C3C),
    onError = Color.White,
    outline = Color(0xFFB0B0B0)
)

// Тёмная тема
private val DarkColorScheme = darkColorScheme(
    primary = ToyPrimaryDark,
    onPrimary = Color(0xFF2D3436),
    primaryContainer = ToyPrimaryDark.copy(alpha = 0.2f),
    secondary = ToySecondaryDark,
    onSecondary = Color(0xFF2D3436),
    tertiary = ToyAccent,
    onTertiary = Color.Black,
    background = ToyBackgroundDark,
    onBackground = ToyOnBackgroundDark,
    surface = ToySurfaceDark,
    onSurface = ToyOnBackgroundDark,
    surfaceVariant = Color(0xFF2D3436),
    onSurfaceVariant = ToyOnBackgroundDark.copy(alpha = 0.7f),
    error = Color(0xFFE74C3C),
    onError = Color.White,
    outline = Color(0xFF555555)
)

// Улучшенная типографика
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun ToyStoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

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
        typography = Typography,
        content = content
    )
}