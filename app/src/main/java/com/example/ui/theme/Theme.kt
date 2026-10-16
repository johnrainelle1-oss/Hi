package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Nexora AI is a dark-first futuristic application
private val NexoraColorScheme = darkColorScheme(
  primary = CyanNeon,
  onPrimary = NavyDarkest,
  primaryContainer = NavyCard,
  onPrimaryContainer = CyanBright,
  secondary = VioletNeon,
  onSecondary = NavyDarkest,
  secondaryContainer = VioletDark,
  onSecondaryContainer = VioletBright,
  tertiary = CyanBright,
  onTertiary = NavyDarkest,
  background = NavyBackground,
  onBackground = TextPrimary,
  surface = NavySurface,
  onSurface = TextPrimary,
  surfaceVariant = NavySurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = NavyBorder,
  outlineVariant = NavySurfaceVariant,
  error = AccentPink,
  onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to futuristic dark aesthetic
  content: @Composable () -> Unit
) {
  val colorScheme = NexoraColorScheme
  val view = LocalView.current

  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
