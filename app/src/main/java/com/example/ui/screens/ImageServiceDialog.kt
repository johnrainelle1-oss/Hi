package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletNeon

@Composable
fun ImageServiceDialog(
  isApiKeyConfigured: Boolean,
  onOpenSetup: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .border(
          width = 1.dp,
          brush = Brush.horizontalGradient(listOf(CyanNeon.copy(alpha = 0.5f), VioletNeon.copy(alpha = 0.5f))),
          shape = RoundedCornerShape(18.dp)
        )
        .background(NavyDarkest)
        .padding(20.dp)
        .testTag("image_service_dialog")
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CyanNeon.copy(alpha = 0.15f))
                .border(1.dp, CyanNeon.copy(alpha = 0.4f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Image Generation",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
              )
              Text(
                text = "Service Availability Check",
                fontSize = 11.sp,
                color = CyanBright
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Connection status card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavySurface)
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
        ) {
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (isApiKeyConfigured) AccentAmber else Color(0xFFEF4444))
              )
              Text(
                text = if (isApiKeyConfigured) "Status: Standby (Limited Beta)" else "Status: Unconnected",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = if (isApiKeyConfigured) AccentAmber else Color(0xFFEF4444)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = if (isApiKeyConfigured) {
                "Text-chat is currently operational via Gemini 3.5 Flash. Real-time Image synthesis requires an allocated image endpoint (e.g. gemini-2.5-flash-image) and dedicated GPU compute quotas."
              } else {
                "No API Key is currently detected. Image generation and AI text chat both require a valid Gemini API key configured in AI Studio Secrets panel."
              },
              fontSize = 12.sp,
              color = TextSecondary,
              lineHeight = 17.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Honest explanation
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(NavyCard)
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = CyanBright,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Per policy, Nexora does not simulate or fake image renders. Generation will activate automatically once your dedicated image provider is connected.",
              fontSize = 11.sp,
              color = TextSecondary,
              lineHeight = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
          ) {
            Text("Got It", fontSize = 13.sp)
          }

          if (!isApiKeyConfigured) {
            Button(
              onClick = {
                onDismiss()
                onOpenSetup()
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = CyanNeon,
                contentColor = NavyDarkest
              )
            ) {
              Text("Connect API", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
    }
  }
}
