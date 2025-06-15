package net.arjunsatarkar.rot13share.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80
)

@Composable
fun Rot13ShareTheme(
    colorScheme: ColorScheme = DarkColorScheme, content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}