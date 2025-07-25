package com.meticha.jetpackboilerplate.launcher.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.ui.generator.ThemeConfig
import kotlin.math.cos
import kotlin.math.sin

/**
 * Routine-specific theme provider that adapts colors and styling based on current routine
 */
@Composable
fun RoutineTheme(
    routineType: RoutineType,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val themeConfig = getThemeConfigForRoutine(routineType)
    val colorScheme = generateRoutineColorScheme(themeConfig, darkTheme)
    
    // Animate color transitions when routine changes
    val animatedColorScheme = animateRoutineColors(colorScheme)
    
    MaterialTheme(
        colorScheme = animatedColorScheme,
        typography = getRoutineTypography(routineType),
        shapes = getRoutineShapes(themeConfig),
        content = content
    )
}

/**
 * Get theme configuration for specific routine
 */
private fun getThemeConfigForRoutine(routineType: RoutineType): ThemeConfig {
    return when (routineType) {
        RoutineType.MORNING -> ThemeConfig(
            name = "Morning Fresh",
            primaryColorHue = 200f, // Soft blue
            saturation = 0.6f,
            brightness = 0.9f,
            cardElevation = 4f,
            cornerRadius = 16f
        )
        RoutineType.AFTERNOON -> ThemeConfig(
            name = "Productive Focus",
            primaryColorHue = 260f, // Deep purple
            saturation = 0.7f,
            brightness = 0.8f,
            cardElevation = 6f,
            cornerRadius = 12f
        )
        RoutineType.EVENING -> ThemeConfig(
            name = "Evening Calm",
            primaryColorHue = 30f, // Warm orange
            saturation = 0.5f,
            brightness = 0.7f,
            cardElevation = 8f,
            cornerRadius = 20f
        )
        RoutineType.WEEKEND -> ThemeConfig(
            name = "Weekend Leisure",
            primaryColorHue = 120f, // Fresh green
            saturation = 0.6f,
            brightness = 0.8f,
            cardElevation = 5f,
            cornerRadius = 18f
        )
        RoutineType.CUSTOM -> ThemeConfig(
            name = "Custom",
            primaryColorHue = 280f, // Neutral purple
            saturation = 0.5f,
            brightness = 0.8f,
            cardElevation = 4f,
            cornerRadius = 16f
        )
    }
}

/**
 * Generate color scheme based on theme configuration
 */
private fun generateRoutineColorScheme(
    themeConfig: ThemeConfig,
    darkTheme: Boolean
): ColorScheme {
    val primaryColor = hsvToColor(
        themeConfig.primaryColorHue,
        themeConfig.saturation,
        if (darkTheme) themeConfig.brightness * 0.8f else themeConfig.brightness
    )
    
    val secondaryColor = hsvToColor(
        (themeConfig.primaryColorHue + 60f) % 360f,
        themeConfig.saturation * 0.8f,
        if (darkTheme) themeConfig.brightness * 0.7f else themeConfig.brightness * 0.9f
    )
    
    val tertiaryColor = hsvToColor(
        (themeConfig.primaryColorHue + 120f) % 360f,
        themeConfig.saturation * 0.6f,
        if (darkTheme) themeConfig.brightness * 0.6f else themeConfig.brightness * 0.8f
    )
    
    return if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            tertiary = tertiaryColor,
            primaryContainer = primaryColor.copy(alpha = 0.2f),
            secondaryContainer = secondaryColor.copy(alpha = 0.2f),
            tertiaryContainer = tertiaryColor.copy(alpha = 0.2f),
            surface = Color(0xFF121212),
            surfaceVariant = Color(0xFF1E1E1E),
            background = Color(0xFF0A0A0A),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onSurface = Color(0xFFE0E0E0),
            onBackground = Color(0xFFE0E0E0)
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            tertiary = tertiaryColor,
            primaryContainer = primaryColor.copy(alpha = 0.1f),
            secondaryContainer = secondaryColor.copy(alpha = 0.1f),
            tertiaryContainer = tertiaryColor.copy(alpha = 0.1f),
            surface = Color(0xFFFFFBFE),
            surfaceVariant = Color(0xFFF5F5F5),
            background = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onSurface = Color(0xFF1C1B1F),
            onBackground = Color(0xFF1C1B1F)
        )
    }
}

/**
 * Animate color transitions between routines
 */
@Composable
private fun animateRoutineColors(colorScheme: ColorScheme): ColorScheme {
    val animationSpec = tween<Color>(durationMillis = 600)
    
    val animatedPrimary by animateColorAsState(
        targetValue = colorScheme.primary,
        animationSpec = animationSpec,
        label = "primary_color"
    )
    
    val animatedSecondary by animateColorAsState(
        targetValue = colorScheme.secondary,
        animationSpec = animationSpec,
        label = "secondary_color"
    )
    
    val animatedTertiary by animateColorAsState(
        targetValue = colorScheme.tertiary,
        animationSpec = animationSpec,
        label = "tertiary_color"
    )
    
    val animatedPrimaryContainer by animateColorAsState(
        targetValue = colorScheme.primaryContainer,
        animationSpec = animationSpec,
        label = "primary_container"
    )
    
    val animatedSecondaryContainer by animateColorAsState(
        targetValue = colorScheme.secondaryContainer,
        animationSpec = animationSpec,
        label = "secondary_container"
    )
    
    val animatedTertiaryContainer by animateColorAsState(
        targetValue = colorScheme.tertiaryContainer,
        animationSpec = animationSpec,
        label = "tertiary_container"
    )
    
    return colorScheme.copy(
        primary = animatedPrimary,
        secondary = animatedSecondary,
        tertiary = animatedTertiary,
        primaryContainer = animatedPrimaryContainer,
        secondaryContainer = animatedSecondaryContainer,
        tertiaryContainer = animatedTertiaryContainer
    )
}

/**
 * Get routine-specific typography
 */
@Composable
private fun getRoutineTypography(routineType: RoutineType) = when (routineType) {
    RoutineType.MORNING -> MaterialTheme.typography // Clean, readable typography
    RoutineType.AFTERNOON -> MaterialTheme.typography // Professional typography
    RoutineType.EVENING -> MaterialTheme.typography // Relaxed typography
    RoutineType.WEEKEND -> MaterialTheme.typography // Casual typography
    RoutineType.CUSTOM -> MaterialTheme.typography // Default typography
}

/**
 * Get routine-specific shapes
 */
@Composable
private fun getRoutineShapes(themeConfig: ThemeConfig) = MaterialTheme.shapes

/**
 * Convert HSV to Color
 */
private fun hsvToColor(hue: Float, saturation: Float, value: Float): Color {
    val c = value * saturation
    val x = c * (1 - kotlin.math.abs(((hue / 60f) % 2) - 1))
    val m = value - c
    
    val (r, g, b) = when ((hue / 60f).toInt()) {
        0 -> Triple(c, x, 0f)
        1 -> Triple(x, c, 0f)
        2 -> Triple(0f, c, x)
        3 -> Triple(0f, x, c)
        4 -> Triple(x, 0f, c)
        5 -> Triple(c, 0f, x)
        else -> Triple(0f, 0f, 0f)
    }
    
    return Color(
        red = (r + m).coerceIn(0f, 1f),
        green = (g + m).coerceIn(0f, 1f),
        blue = (b + m).coerceIn(0f, 1f),
        alpha = 1f
    )
}

/**
 * Get routine-specific accent colors for UI elements
 */
@Composable
fun getRoutineAccentColor(routineType: RoutineType): Color {
    return when (routineType) {
        RoutineType.MORNING -> MaterialTheme.colorScheme.primary
        RoutineType.AFTERNOON -> MaterialTheme.colorScheme.secondary
        RoutineType.EVENING -> MaterialTheme.colorScheme.tertiary
        RoutineType.WEEKEND -> MaterialTheme.colorScheme.outline
        RoutineType.CUSTOM -> MaterialTheme.colorScheme.onSurface
    }
}

/**
 * Get routine-specific card colors
 */
@Composable
fun getRoutineCardColor(routineType: RoutineType): Color {
    return when (routineType) {
        RoutineType.MORNING -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        RoutineType.AFTERNOON -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
        RoutineType.EVENING -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f)
        RoutineType.WEEKEND -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        RoutineType.CUSTOM -> MaterialTheme.colorScheme.surface
    }
}

/**
 * Get routine-specific icon colors
 */
@Composable
fun getRoutineIconColor(routineType: RoutineType): Color {
    return when (routineType) {
        RoutineType.MORNING -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        RoutineType.AFTERNOON -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
        RoutineType.EVENING -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
        RoutineType.WEEKEND -> MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
        RoutineType.CUSTOM -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }
}