package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyQuotaState
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.VioletBright
import com.example.ui.theme.VioletNeon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmptyChatView(
  quotaState: DailyQuotaState,
  onPromptSelected: (String) -> Unit,
  onOpenUpgrade: () -> Unit,
  modifier: Modifier = Modifier
) {
  val suggestions = listOf(
    PromptSuggestion(
      icon = Icons.Default.Bolt,
      title = "Explain Quantum Computing",
      prompt = "Explain quantum computing in simple terms with an intuitive real-world analogy."
    ),
    PromptSuggestion(
      icon = Icons.Default.Code,
      title = "Android Architecture",
      prompt = "Show a modern Kotlin Jetpack Compose pattern for unidirectional data flow with ViewModel."
    ),
    PromptSuggestion(
      icon = Icons.Default.RocketLaunch,
      title = "AI Startup Ideas",
      prompt = "Brainstorm 3 high-impact startup ideas combining AI agents with developer tooling."
    ),
    PromptSuggestion(
      icon = Icons.Default.AutoAwesome,
      title = "Sci-Fi Concept",
      prompt = "Write a compelling futuristic micro-story about an AI that discovers human curiosity."
    )
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    // Glowing Logo Hero
    NexoraLogo(
      size = 72.dp,
      withGlow = true,
      animateGlow = true
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "NEXORA AI",
      fontWeight = FontWeight.Black,
      fontSize = 24.sp,
      letterSpacing = 2.sp,
      color = TextPrimary
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = "Your intelligence. Your future.",
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium,
      color = CyanBright
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Plan & Quota Status Badge
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .border(
          width = 1.dp,
          brush = Brush.horizontalGradient(listOf(CyanNeon.copy(alpha = 0.5f), VioletNeon.copy(alpha = 0.5f))),
          shape = RoundedCornerShape(20.dp)
        )
        .background(NavySurface)
        .clickable { onOpenUpgrade() }
        .padding(horizontal = 14.dp, vertical = 6.dp)
        .testTag("plan_status_chip")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = CyanNeon,
          modifier = Modifier.size(14.dp)
        )
        Text(
          text = if (quotaState.isProMode) {
            "Nexora Pro Active • Unlimited Messages"
          } else {
            "Free Plan • ${quotaState.remainingToday} of ${quotaState.dailyLimit} messages left today"
          },
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
      }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Text(
      text = "START A CONVERSATION",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.5.sp,
      color = TextTertiary,
      modifier = Modifier.fillMaxWidth(),
      textAlign = TextAlign.Start
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Suggestion Cards
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      for (item in suggestions) {
        SuggestionCard(
          item = item,
          onClick = { onPromptSelected(item.prompt) }
        )
      }
    }
  }
}

private data class PromptSuggestion(
  val icon: ImageVector,
  val title: String,
  val prompt: String
)

@Composable
private fun SuggestionCard(
  item: PromptSuggestion,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
      .background(NavyCard)
      .clickable { onClick() }
      .padding(12.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(CyanNeon.copy(alpha = 0.15f))
          .border(1.dp, CyanNeon.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = item.icon,
          contentDescription = null,
          tint = CyanNeon,
          modifier = Modifier.size(18.dp)
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.title,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp,
          color = TextPrimary
        )
        Text(
          text = item.prompt,
          fontSize = 11.sp,
          color = TextSecondary,
          maxLines = 1
        )
      }

      Text(
        text = "→",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = CyanBright
      )
    }
  }
}
