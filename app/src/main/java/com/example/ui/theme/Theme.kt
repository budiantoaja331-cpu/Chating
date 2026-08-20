package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
      primary = PinkPrimaryDark,
      onPrimary = PinkOnPrimaryDark,
      secondary = PinkSecondaryDark,
      tertiary = PinkTertiaryDark,
      background = PinkBackgroundDark,
      surface = PinkSurfaceDark,
      onBackground = PinkOnBackgroundDark,
      onSurface = PinkOnBackgroundDark
  )

private val LightColorScheme =
  lightColorScheme(
      primary = PinkPrimaryLight,
      onPrimary = PinkOnPrimaryLight,
      secondary = PinkSecondaryLight,
      tertiary = PinkTertiaryLight,
      background = PinkBackgroundLight,
      surface = PinkSurfaceLight,
      onBackground = PinkOnBackgroundLight,
      onSurface = PinkOnBackgroundLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // FORCE LIGHT THEME (White Background)
  // Disable dynamic color by default so the Pink theme is always forced
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
