package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyQuotaState
import com.example.ui.components.NexoraLogo
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumUpgradeSheet(
  quotaState: DailyQuotaState,
  onToggleDemoPro: (Boolean) -> Unit,
  onResetQuota: () -> Unit,
  onJoinWaitlist: () -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var waitlistJoined by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = NavyDarkest,
    scrimColor = Color.Black.copy(alpha = 0.7f),
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 10.dp, bottom = 6.dp)
          .size(width = 36.dp, height = 4.dp)
          .clip(CircleShape)
          .background(CyanNeon.copy(alpha = 0.4f))
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(bottom = 36.dp)
        .testTag("premium_upgrade_sheet")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          NexoraLogo(size = 32.dp, withGlow = true)
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Upgrade Intelligence",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Current Plan Status Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(NavySurface)
          .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
          .padding(14.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (quotaState.isProMode) "Active Plan: Nexora Pro (Demo)" else "Active Plan: Free Tier",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = if (quotaState.isProMode) AccentAmber else CyanBright
            )
            Text(
              text = if (quotaState.isProMode) "Unlimited" else "${quotaState.remainingToday} / 50 Left",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimary
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Progress indicator for Free Tier
          if (!quotaState.isProMode) {
            val progress = (quotaState.usedToday.toFloat() / quotaState.dailyLimit.toFloat()).coerceIn(0f, 1f)
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(NavySurfaceVariant)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(progress)
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp))
                  .background(
                    Brush.horizontalGradient(
                      if (quotaState.isExhausted) listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                      else listOf(CyanNeon, VioletNeon)
                    )
                  )
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Free accounts receive 50 high-intelligence text messages every 24 hours.",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Tier Cards Grid
      Text(
        text = "PLANS & CAPABILITIES",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextTertiary
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Pro Plan Card
      PlanCard(
        title = "Nexora Pro",
        price = "$19 / month",
        highlighted = true,
        features = listOf(
          PlanFeature("Unlimited text messages daily", true),
          PlanFeature("3x faster response speed (Priority Queue)", true),
          PlanFeature("1M token context memory", true),
          PlanFeature("Code synthesizer & deep reasoning mode", true),
          PlanFeature("Advanced Reasoning (Gemini 3.1 Pro)", false, isComingSoon = true),
          PlanFeature("Veo Video Generation", false, isComingSoon = true)
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Ultra Plan Card
      PlanCard(
        title = "Nexora Ultra",
        price = "$49 / month",
        highlighted = false,
        features = listOf(
          PlanFeature("Everything in Nexora Pro", true),
          PlanFeature("Direct API access & developer SDK keys", false, isComingSoon = true),
          PlanFeature("High-Resolution 4K Image Generation", false, isComingSoon = true),
          PlanFeature("Cinematic Veo Fast Video Generator", false, isComingSoon = true)
        )
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Transparent Notice & Demo Pro Switch
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFF131A2F))
          .border(1.dp, VioletNeon.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
          .padding(14.dp)
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = VioletBright,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "Prototype Notice & Simulation",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = VioletBright
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Real billing gateways are not active in this prototype. You can simulate the Pro experience with unlimited messages or join the early waitlist below.",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Demo Mode Toggle Switch
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Simulate Nexora Pro (Demo)",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = TextPrimary
              )
              Text(
                text = "Enables unlimited daily messaging",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }

            Switch(
              checked = quotaState.isProMode,
              onCheckedChange = { onToggleDemoPro(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = NavyDarkest,
                checkedTrackColor = CyanNeon,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = NavySurfaceVariant
              ),
              modifier = Modifier.testTag("demo_pro_switch")
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action Buttons
      Button(
        onClick = {
          waitlistJoined = true
          onJoinWaitlist()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("join_waitlist_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (waitlistJoined) AccentGreen else CyanNeon,
          contentColor = NavyDarkest
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = if (waitlistJoined) "✓ You're on the Pro Waitlist!" else "Join Nexora Pro Waitlist",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedButton(
        onClick = onResetQuota,
        modifier = Modifier
          .fillMaxWidth()
          .height(42.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = TextSecondary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(
          imageVector = Icons.Default.RestartAlt,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Reset Free Quota for Testing (0/50)", fontSize = 12.sp)
      }
    }
  }
}

private data class PlanFeature(
  val label: String,
  val isIncluded: Boolean,
  val isComingSoon: Boolean = false
)

@Composable
private fun PlanCard(
  title: String,
  price: String,
  highlighted: Boolean,
  features: List<PlanFeature>
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .border(
        width = 1.dp,
        brush = if (highlighted) {
          Brush.horizontalGradient(listOf(CyanNeon, VioletNeon))
        } else {
          Brush.horizontalGradient(listOf(NavyBorder, NavyBorder))
        },
        shape = RoundedCornerShape(14.dp)
      )
      .background(if (highlighted) NavyCard else NavySurface)
      .padding(14.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (highlighted) CyanBright else TextPrimary
          )
          if (highlighted) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(VioletNeon.copy(alpha = 0.25f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "POPULAR",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = VioletBright
              )
            }
          }
        }

        Text(
          text = price,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = TextPrimary
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (f in features) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (f.isComingSoon) {
              Icon(
                imageVector = Icons.Default.HourglassTop,
                contentDescription = null,
                tint = AccentAmber,
                modifier = Modifier.size(14.dp)
              )
            } else {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(14.dp)
              )
            }

            Text(
              text = f.label,
              fontSize = 12.sp,
              color = if (f.isComingSoon) TextTertiary else TextSecondary,
              modifier = Modifier.weight(1f)
            )

            if (f.isComingSoon) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(AccentAmber.copy(alpha = 0.2f))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "COMING SOON",
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = AccentAmber
                )
              }
            }
          }
        }
      }
    }
  }
}
