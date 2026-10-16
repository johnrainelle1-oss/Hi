package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.VioletGlow
import com.example.ui.theme.VioletNeon

@Composable
fun NexoraLogo(
  modifier: Modifier = Modifier,
  size: Dp = 40.dp,
  withGlow: Boolean = true,
  animateGlow: Boolean = false
) {
  val infiniteTransition = rememberInfiniteTransition(label = "logo_glow")
  val glowAlpha by if (animateGlow) {
    infiniteTransition.animateFloat(
      initialValue = 0.4f,
      targetValue = 0.9f,
      animationSpec = infiniteRepeatable(
        animation = tween(1400, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "glow_alpha"
    )
  } else {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.7f) }
  }

  val outerGlowColor = CyanNeon.copy(alpha = if (withGlow) glowAlpha * 0.4f else 0f)
  val innerBorderBrush = Brush.linearGradient(
    colors = listOf(CyanNeon, VioletNeon)
  )

  Box(
    modifier = modifier
      .size(size)
      .shadow(
        elevation = if (withGlow) 10.dp else 0.dp,
        shape = CircleShape,
        ambientColor = CyanNeon,
        spotColor = VioletNeon
      )
      .background(outerGlowColor, CircleShape)
      .padding(2.dp)
      .border(1.5.dp, innerBorderBrush, CircleShape)
      .clip(CircleShape)
      .background(NavyDarkest),
    contentAlignment = Alignment.Center
  ) {
    Image(
      painter = painterResource(id = R.drawable.nexora_logo),
      contentDescription = "Nexora Glowing Logo",
      modifier = Modifier
        .fillMaxSize()
        .clip(CircleShape),
      contentScale = ContentScale.Crop
    )
  }
}
