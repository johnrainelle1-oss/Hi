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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyQuotaState
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentPink
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.VioletBright
import com.example.ui.theme.VioletNeon

@Composable
fun ChatInputBar(
  text: String,
  onTextChange: (String) -> Unit,
  onSend: () -> Unit,
  isGenerating: Boolean,
  quotaState: DailyQuotaState,
  onOpenImageService: () -> Unit,
  onOpenUpgrade: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isExhausted = quotaState.isExhausted
  val canSend = text.isNotBlank() && !isGenerating && !isExhausted

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(NavyDarkest)
      .navigationBarsPadding()
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    // If limit exhausted, show prompt to upgrade
    if (isExhausted) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(AccentPink.copy(alpha = 0.15f))
          .border(1.dp, AccentPink.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
          .clickable { onOpenUpgrade() }
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = AccentPink,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Daily limit reached (50/50 messages).",
              color = AccentPink,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Text(
            text = "Upgrade Pro →",
            color = CyanBright,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Input Bar Cyber Container
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(
          elevation = 8.dp,
          shape = RoundedCornerShape(24.dp),
          ambientColor = CyanNeon.copy(alpha = 0.3f),
          spotColor = VioletNeon.copy(alpha = 0.2f)
        )
        .border(
          width = 1.dp,
          brush = Brush.horizontalGradient(
            listOf(
              CyanNeon.copy(alpha = if (text.isNotBlank()) 0.8f else 0.35f),
              VioletNeon.copy(alpha = if (text.isNotBlank()) 0.6f else 0.25f)
            )
          ),
          shape = RoundedCornerShape(24.dp)
        )
        .clip(RoundedCornerShape(24.dp))
        .background(NavySurface)
        .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Image generation icon (service status indicator)
        IconButton(
          onClick = onOpenImageService,
          modifier = Modifier
            .size(40.dp)
            .testTag("image_service_button")
        ) {
          Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Image Generation Service",
            tint = CyanBright.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
          )
        }

        // Message input text field
        Box(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
          if (text.isEmpty()) {
            Text(
              text = if (isExhausted) "Daily free limit reached..." else "Message Nexora AI...",
              color = TextTertiary,
              fontSize = 14.sp
            )
          }

          BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("message_input"),
            textStyle = TextStyle(
              color = TextPrimary,
              fontSize = 14.sp,
              lineHeight = 20.sp
            ),
            cursorBrush = SolidColor(CyanNeon),
            keyboardOptions = KeyboardOptions(
              imeAction = if (canSend) ImeAction.Send else ImeAction.Default
            ),
            keyboardActions = KeyboardActions(
              onSend = {
                if (canSend) onSend()
              }
            ),
            maxLines = 4,
            enabled = !isExhausted
          )
        }

        // Send Button
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
              if (canSend) {
                Brush.linearGradient(listOf(CyanNeon, VioletNeon))
              } else {
                SolidColor(NavySurfaceVariant)
              }
            )
            .clickable(enabled = canSend) { onSend() }
            .testTag("send_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send message",
            tint = if (canSend) NavyDarkest else TextTertiary,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // Micro Quota Status & Tagline Footer
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (quotaState.isProMode) {
          "Nexora Pro Active • High Performance"
        } else {
          "${quotaState.remainingToday} / ${quotaState.dailyLimit} daily messages left"
        },
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = if (quotaState.isExhausted) AccentPink else TextSecondary
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onOpenUpgrade() }
      ) {
        Text(
          text = if (quotaState.isProMode) "Pro Settings" else "Free Plan",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = if (quotaState.isProMode) AccentAmber else CyanBright
        )
      }
    }
  }
}
