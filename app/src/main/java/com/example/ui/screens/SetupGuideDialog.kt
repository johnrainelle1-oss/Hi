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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.ApiKeyStatus
import com.example.ui.theme.AccentGreen
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
import com.example.ui.theme.VioletNeon

@Composable
fun SetupGuideDialog(
  apiKeyStatus: ApiKeyStatus,
  onSaveKey: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var keyInput by remember { mutableStateOf("") }

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
        .testTag("setup_guide_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Header
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
                imageVector = Icons.Default.Key,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Nexora Intelligence Setup",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
              )
              Text(
                text = "Real Gemini AI Integration",
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

        Spacer(modifier = Modifier.height(14.dp))

        // Current Key Status Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavySurface)
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = if (apiKeyStatus.isConfigured) Icons.Default.CheckCircle else Icons.Default.Warning,
              contentDescription = null,
              tint = if (apiKeyStatus.isConfigured) AccentGreen else AccentPink,
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = if (apiKeyStatus.isConfigured) "Gemini API Key Connected" else "API Key Not Configured",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = if (apiKeyStatus.isConfigured) AccentGreen else AccentPink
              )
              Text(
                text = if (apiKeyStatus.isConfigured) "Key: ${apiKeyStatus.keyPreview} (Ready)" else "Missing key. Live AI calls will fail until configured.",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step by Step Instructions
        Text(
          text = "HOW TO CONNECT REAL AI:",
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          letterSpacing = 1.sp,
          color = CyanBright
        )

        Spacer(modifier = Modifier.height(8.dp))

        InstructionStep(
          number = "1",
          title = "Get a free Google Gemini API Key",
          detail = "Visit Google AI Studio (aistudio.google.com) to generate your free personal API key."
        )

        Spacer(modifier = Modifier.height(8.dp))

        InstructionStep(
          number = "2",
          title = "Set GEMINI_API_KEY in Secrets Panel",
          detail = "In AI Studio, click the Secrets icon in the left toolbar and add GEMINI_API_KEY with your key value."
        )

        Spacer(modifier = Modifier.height(8.dp))

        InstructionStep(
          number = "3",
          title = "Or Paste Key Below For Instant Testing",
          detail = "You can also paste your Gemini API key below to test Nexora immediately without rebuilding."
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Direct Key Input for Immediate Testing
        OutlinedTextField(
          value = keyInput,
          onValueChange = { keyInput = it },
          placeholder = { Text("AIzaSy... (Paste Gemini Key)", color = TextSecondary, fontSize = 12.sp) },
          label = { Text("Direct API Key (Optional)", fontSize = 12.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("api_key_input"),
          singleLine = true,
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanNeon,
            unfocusedBorderColor = NavyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = NavySurface,
            unfocusedContainerColor = NavySurface
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              if (keyInput.isNotBlank()) {
                onSaveKey(keyInput.trim())
                keyInput = ""
              }
              onDismiss()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = CyanNeon,
              contentColor = NavyDarkest
            )
          ) {
            Text(
              text = if (keyInput.isNotBlank()) "Save & Test Key" else "Understood",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun InstructionStep(
  number: String,
  title: String,
  detail: String
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(NavySurfaceVariant.copy(alpha = 0.6f))
      .padding(10.dp)
  ) {
    Row(verticalAlignment = Alignment.Top) {
      Box(
        modifier = Modifier
          .size(20.dp)
          .clip(CircleShape)
          .background(CyanNeon)
          .padding(top = 1.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = number,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = NavyDarkest
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(
          text = title,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          color = TextPrimary
        )
        Text(
          text = detail,
          fontSize = 11.sp,
          color = TextSecondary,
          lineHeight = 15.sp
        )
      }
    }
  }
}
