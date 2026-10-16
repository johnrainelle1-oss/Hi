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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatSession
import com.example.ui.theme.AccentPink
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletNeon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistorySheet(
  sessions: List<ChatSession>,
  currentSessionId: String,
  onSelectSession: (String) -> Unit,
  onDeleteSession: (String) -> Unit,
  onNewChat: () -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = NavyDarkest,
    scrimColor = Color.Black.copy(alpha = 0.65f),
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
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
        .testTag("chat_history_sheet")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Conversation History",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = "${sessions.size} sessions stored locally",
            fontSize = 12.sp,
            color = TextSecondary
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

      // New Chat Button
      Button(
        onClick = onNewChat,
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
          .testTag("new_chat_sheet_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = CyanNeon,
          contentColor = NavyDarkest
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Start New Chat",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (sessions.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No previous conversations yet.",
            color = TextSecondary,
            fontSize = 13.sp
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(sessions, key = { it.id }) { session ->
            val isSelected = session.id == currentSessionId
            val timeText = remember(session.lastUpdatedAt) {
              SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(session.lastUpdatedAt))
            }

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(
                  width = 1.dp,
                  brush = if (isSelected) {
                    Brush.horizontalGradient(listOf(CyanNeon, VioletNeon))
                  } else {
                    Brush.horizontalGradient(listOf(NavyBorder, NavyBorder))
                  },
                  shape = RoundedCornerShape(12.dp)
                )
                .background(if (isSelected) NavyCard else NavySurface)
                .clickable { onSelectSession(session.id) }
                .padding(12.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.ChatBubbleOutline,
                  contentDescription = null,
                  tint = if (isSelected) CyanNeon else TextSecondary,
                  modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = session.title,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                    color = if (isSelected) CyanBright else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  if (session.preview.isNotBlank()) {
                    Text(
                      text = session.preview,
                      fontSize = 11.sp,
                      color = TextSecondary,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                  Text(
                    text = timeText,
                    fontSize = 10.sp,
                    color = TextSecondary.copy(alpha = 0.7f)
                  )
                }

                IconButton(
                  onClick = { onDeleteSession(session.id) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete session",
                    tint = AccentPink.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
