package com.ichen.charlie_chatgpt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val DarkColorPalette = darkColors(
    primary = PastelRed,
    onPrimary = VoidBlack,
    background = VoidBlack,
    onBackground = BleachWhite,
    surface = StormyGray,
    onSurface = BleachWhite,
)

private val LightColorPalette = lightColors(
    primary = PastelRed,
    onPrimary = VoidBlack,
    background = BleachWhite,
    onBackground = VoidBlack,
    surface = LightGray,
    onSurface = VoidBlack,
)

@Composable
fun CharlieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    CharlieTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

val LocalCustomColors = staticCompositionLocalOf {
    DarkColorPalette
}
val LocalCustomTypography = staticCompositionLocalOf {
    Typography
}
val LocalCustomShapes = staticCompositionLocalOf {
    Shapes
}

@Composable
fun CharlieTheme(
    colors: Colors = CharlieTheme.colors,
    typography: Typography = CharlieTheme.typography,
    shapes: Shapes = CharlieTheme.shapes,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalCustomColors provides colors,
        LocalCustomTypography provides typography,
        LocalCustomShapes provides shapes,
        content = content
    )
}

object CharlieTheme {
    /**
     * Retrieves the current [Colors] at the call site's position in the hierarchy.
     *
     * @sample androidx.compose.material.samples.ThemeColorSample
     */
    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomColors.current

    /**
     * Retrieves the current [Typography] at the call site's position in the hierarchy.
     *
     * @sample androidx.compose.material.samples.ThemeTextStyleSample
     */
    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomTypography.current

    /**
     * Retrieves the current [Shapes] at the call site's position in the hierarchy.
     */
    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomShapes.current
}