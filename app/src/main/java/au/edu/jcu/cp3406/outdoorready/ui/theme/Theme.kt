package au.edu.jcu.cp3406.outdoorready.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F5BC8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9E3FF),
    onPrimaryContainer = Color(0xFF001944),
    secondary = Color(0xFF2F7D4D),
    secondaryContainer = Color(0xFFDDF3E4),
    onSecondaryContainer = Color(0xFF123321),
    tertiary = Color(0xFFB26A00),
    tertiaryContainer = Color(0xFFFFE8C2),
    onTertiaryContainer = Color(0xFF4F2D00),
    errorContainer = Color(0xFFFFDDD8),
    onErrorContainer = Color(0xFF5C150F),
    surface = Color(0xFFFFFBFF),
    surfaceVariant = Color(0xFFE8EDF5),
    background = Color(0xFFF5F7FB),
    outline = Color(0xFFBEC7D4),
    outlineVariant = Color(0xFFD7DEE9),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFFA5D6A7),
    tertiary = Color(0xFFFFE082),
)

@Composable
fun OutdoorReadyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
