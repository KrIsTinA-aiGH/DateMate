package com.example.datemate.ui.theme

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

// ========== ЦВЕТА НА ОСНОВЕ ЛОГОТИПА ==========

// Оранжевый (календарь с датой) — главный акцент
val OrangePrimary = Color(0xFFF57C20)
val OrangePrimaryVariant = Color(0xFFE06510)
val OrangeOnPrimary = Color(0xFFFFFFFF)

// Зелёный (яблоко) — вторичный акцент
val GreenSecondary = Color(0xFF7CB342)
val GreenSecondaryVariant = Color(0xFF689F38)
val GreenOnSecondary = Color(0xFFFFFFFF)

// Тёмно-синий графит (фон холодильника)
val DarkGraphite = Color(0xFF1A2332)
val DarkGraphiteSurface = Color(0xFF243044)
val DarkGraphiteVariant = Color(0xFF2D3A52)

// ========== СВЕТЛАЯ ТЕМА ==========
private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = OrangeOnPrimary,
    primaryContainer = Color(0xFFFFD9B3), // Светло-оранжевый для выбранных элементов

    secondary = GreenSecondary,
    onSecondary = GreenOnSecondary,
    secondaryContainer = Color(0xFFD4E8B8), // Светло-зелёный для акцентов

    tertiary = Color(0xFF5C6BC0),
    onTertiary = Color(0xFFFFFFFF),

    background = Color(0xFFD8D8E0), // Более тёмный серый фон
    onBackground = Color(0xFF1A1A1E),

    surface = Color(0xFFE8E8F0), // Серые карточки (не белые)
    onSurface = Color(0xFF1A1A1E),
    surfaceVariant = Color(0xFFC8C8D4), // Более тёмный серый для вариаций
    onSurfaceVariant = Color(0xFF3A3A42),

    outline = Color(0xFF9090A0), // Тёмно-серые обводки
    outlineVariant = Color(0xFFA0A0B0),

    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF)
)

// ========== ТЁМНАЯ ТЕМА ==========
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF9A4A),
    onPrimary = Color(0xFF5C2E00),
    primaryContainer = Color(0xFF7A3E10),
    onPrimaryContainer = Color(0xFFFFE0C2),

    secondary = Color(0xFF9CCC65),
    onSecondary = Color(0xFF2D4A12),
    secondaryContainer = Color(0xFF4A6B28),
    onSecondaryContainer = Color(0xFFE8F5D4),

    tertiary = Color(0xFF9FA8DA),
    onTertiary = Color(0xFF28306B),

    background = DarkGraphite,
    onBackground = Color(0xFFE8E8EC),

    surface = DarkGraphiteSurface,
    onSurface = Color(0xFFE8E8EC),
    surfaceVariant = DarkGraphiteVariant,
    onSurfaceVariant = Color(0xFFB0B0BC),

    outline = Color(0xFF4A5568),
    outlineVariant = Color(0xFF3A4558),

    error = Color(0xFFEF5350),
    onError = Color(0xFF690005)
)

@Composable
fun DateMateTheme(
    themeType: Int = 2,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isSystemDark = isSystemInDarkTheme()

    val useDarkTheme = when (themeType) {
        0 -> false
        1 -> true
        else -> isSystemDark
    }

    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}