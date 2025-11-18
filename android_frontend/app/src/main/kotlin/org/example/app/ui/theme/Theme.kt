package org.example.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Ocean Professional palette
val Blue500 = Color(0xFF2563EB) // primary
val Amber500 = Color(0xFFF59E0B) // secondary / accent
val Error500 = Color(0xFFEF4444)
val Background = Color(0xFFF9FAFB)
val Surface = Color(0xFFFFFFFF)
val Text = Color(0xFF111827)

private val LightColors = lightColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    secondary = Amber500,
    onSecondary = Color(0xFF1F2937),
    error = Error500,
    onError = Color.White,
    background = Background,
    onBackground = Text,
    surface = Surface,
    onSurface = Text
)

private val DarkColors = darkColorScheme(
    primary = Blue500,
    secondary = Amber500,
    error = Error500
)

// PUBLIC_INTERFACE
@Composable
fun NoteEaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
