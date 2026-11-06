package br.com.easyshop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF1B5E9E),
    secondary = Color(0xFF2E7D5B),
    tertiary = Color(0xFFB45309)
)
private val Dark = darkColorScheme(
    primary = Color(0xFF9CC7F2),
    secondary = Color(0xFF8FD3B2),
    tertiary = Color(0xFFF5B57A)
)

@Composable
fun EasyShopTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
