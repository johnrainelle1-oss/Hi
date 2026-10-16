package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyQuotaState
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletBright
import com.example.ui.theme.VioletNeon

@Composable
fun NexoraTopBar(
  quotaState: DailyQuotaState,
  onNewChat: () -> Unit,
  onOpenHistory: () -> Unit,
  onOpenUpgrade: () -> Unit,
  onOpenSetup: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(NavyDarkest)
      .statusBarsPadding()
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Logo & Brand
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { onOpenSetup() }
          .padding(4.dp)
      ) {
        NexoraLogo(
          size = 38.dp,
          withGlow = true
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "NEXORA",
              fontWeight = FontWeight.Black,
              fontSize = 17.sp,
              letterSpacing = 1.sp,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  Brush.horizontalGradient(listOf(CyanNeon, VioletNeon))
                )
                .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
              Text(
                text = "AI",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDarkest
              )
            }
          }

          Text(
            text = "Your intelligence. Your future.",
            fontSize = 10.sp,
            color = CyanBright.copy(alpha = 0.85f),
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Right: Actions (Quota Pill, New Chat, History)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Quota Badge Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
              width = 1.dp,
              brush = if (quotaState.isProMode) {
                Brush.horizontalGradient(listOf(Color(0xFFF59E0B), VioletBright))
              } else {
                Brush.horizontalGradient(listOf(CyanNeon.copy(alpha = 0.6f), NavyBorder))
              },
              shape = RoundedCornerShape(12.dp)
            )
            .background(NavyCard)
            .clickable { onOpenUpgrade() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("quota_badge"),
          contentAlignment = Alignment.Center
        ) {
          if (quotaState.isProMode) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = "PRO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF59E0B)
              )
            }
          } else {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .background(
                    if (quotaState.isExhausted) Color(0xFFEF4444) else CyanNeon,
                    CircleShape
                  )
              )
              Text(
                text = "${quotaState.remainingToday} left",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (quotaState.isExhausted) Color(0xFFEF4444) else CyanBright
              )
            }
          }
        }

        // New Chat Button
        IconButton(
          onClick = onNewChat,
          modifier = Modifier
            .size(36.dp)
            .border(1.dp, CyanNeon.copy(alpha = 0.4f), CircleShape)
            .background(NavySurface, CircleShape)
            .testTag("new_chat_button")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "New chat",
            tint = CyanNeon,
            modifier = Modifier.size(18.dp)
          )
        }

        // Sessions Drawer
        IconButton(
          onClick = onOpenHistory,
          modifier = Modifier
            .size(36.dp)
            .border(1.dp, NavyBorder, CircleShape)
            .background(NavySurface, CircleShape)
            .testTag("chat_history_button")
        ) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = "Chat sessions",
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
