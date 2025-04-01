package eg.iti.mad.climaguard.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6200EE), // Purple
    secondary = Color(0xFF03DAC6), // Teal
    tertiary = Color(0xFFFF4081), // Pink
    background = Color(0xFF121212), // Dark background
    surface = Color(0xFF1E1E1E), // Dark surface
    onPrimary = Color.White, // Text on primary
    onSecondary = Color.Black, // Text on secondary
    onTertiary = Color.White, // Text on tertiary
    onBackground = Color.White, // Text on background
    onSurface = Color.White // Text on surface
)

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF284486), // Purple
    secondary = Color(0xFF665B57), // Teal
    tertiary = Color(0xFFFF4081), // Pink
    background = Color(0xFFEEEEEE), // Light background
    surface = Color(0xFFFFFFFF), // Light surface
    onPrimary = Color.White, // Text on primary
    onSecondary = Color.Black, // Text on secondary
    onTertiary = Color.Black, // Text on tertiary
    onBackground = Color.Black, // Text on background
    onSurface = Color.Black // Text on surface
)

@Composable
fun ClimaGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
