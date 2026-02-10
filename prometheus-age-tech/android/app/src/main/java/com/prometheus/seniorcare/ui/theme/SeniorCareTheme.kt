package com.prometheus.seniorcare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    error = Color(0xFFD32F2F)
)

@Composable
fun SeniorCareTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
