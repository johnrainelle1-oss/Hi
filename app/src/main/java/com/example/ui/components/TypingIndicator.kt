package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletNeon
import kotlinx.coroutines.delay

@Composable
fun TypingIndicator(
  modifier: Modifier = Modifier
) {
  val scale1 = remember { Animatable(0.4f) }
  val scale2 = remember { Animatable(0.4f) }
  val scale3 = remember { Animatable(0.4f) }

  LaunchedEffect(Unit) {
    val animSpec = infiniteRepeatable<Float>(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    )

    launchAnimation(scale1, 0, animSpec)
  }

  LaunchedEffect(Unit) {
    delay(200)
    val animSpec = infiniteRepeatable<Float>(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    )
    launchAnimation(scale2, 200, animSpec)
  }

  LaunchedEffect(Unit) {
    delay(400)
    val animSpec = infiniteRepeatable<Float>(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    )
    launchAnimation(scale3, 400, animSpec)
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("typing_indicator"),
    verticalAlignment = Alignment.Top
  ) {
    NexoraLogo(
      size = 32.dp,
      withGlow = true,
      animateGlow = true
    )

    Spacer(modifier = Modifier.width(10.dp))

    Column {
      Box(
        modifier = Modifier
          .border(
            width = 1.dp,
            brush = Brush.horizontalGradient(listOf(CyanNeon.copy(alpha = 0.5f), VioletNeon.copy(alpha = 0.3f))),
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
          )
          .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
          .background(NavyCard)
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .scale(scale1.value)
              .background(CyanNeon, CircleShape)
          )
          Box(
            modifier = Modifier
              .size(8.dp)
              .scale(scale2.value)
              .background(CyanBright, CircleShape)
          )
          Box(
            modifier = Modifier
              .size(8.dp)
              .scale(scale3.value)
              .background(VioletNeon, CircleShape)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Nexora is synthesizing intelligence...",
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = CyanBright.copy(alpha = 0.7f),
        modifier = Modifier.padding(start = 2.dp)
      )
    }
  }
}

private suspend fun launchAnimation(
  anim: Animatable<Float, *>,
  delayMs: Long,
  spec: androidx.compose.animation.core.InfiniteRepeatableSpec<Float>
) {
  anim.animateTo(
    targetValue = 1.1f,
    animationSpec = spec
  )
}
