package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.model.MessageStatus
import com.example.ui.theme.AccentPink
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.VioletBright
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletDark
import com.example.ui.theme.VioletNeon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageItem(
  message: ChatMessage,
  onRetry: () -> Unit,
  onOpenSetup: () -> Unit,
  onCopied: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val timeString = remember(message.timestamp) {
    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))
  }

  when (message.sender) {
    MessageSender.USER -> {
      UserMessageBubble(
        message = message,
        timeString = timeString,
        modifier = modifier
      )
    }
    MessageSender.AI -> {
      if (message.status == MessageStatus.ERROR) {
        ErrorMessageBubble(
          message = message,
          onRetry = onRetry,
          onOpenSetup = onOpenSetup,
          modifier = modifier
        )
      } else {
        AiMessageBubble(
          message = message,
          timeString = timeString,
          onCopy = {
            copyToClipboard(context, message.content)
            onCopied("Message copied to clipboard")
          },
          modifier = modifier
        )
      }
    }
    MessageSender.SYSTEM -> {
      SystemMessageBubble(message = message, modifier = modifier)
    }
  }
}

@Composable
private fun UserMessageBubble(
  message: ChatMessage,
  timeString: String,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("user_message_${message.id}"),
    horizontalArrangement = Arrangement.End
  ) {
    Column(
      horizontalAlignment = Alignment.End,
      modifier = Modifier.widthIn(max = 310.dp)
    ) {
      Box(
        modifier = Modifier
          .clip(
            RoundedCornerShape(
              topStart = 18.dp,
              topEnd = 4.dp,
              bottomStart = 18.dp,
              bottomEnd = 18.dp
            )
          )
          .background(
            Brush.linearGradient(
              colors = listOf(
                Color(0xFF7C3AED),
                Color(0xFF9333EA)
              )
            )
          )
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Text(
          text = message.content,
          color = Color.White,
          fontSize = 15.sp,
          lineHeight = 22.sp
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = timeString,
        fontSize = 11.sp,
        color = TextSecondary.copy(alpha = 0.8f),
        modifier = Modifier.padding(end = 4.dp)
      )
    }
  }
}

@Composable
private fun AiMessageBubble(
  message: ChatMessage,
  timeString: String,
  onCopy: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("ai_message_${message.id}"),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.Top
  ) {
    NexoraLogo(
      size = 32.dp,
      withGlow = false
    )

    Spacer(modifier = Modifier.width(10.dp))

    Column(
      modifier = Modifier
        .widthIn(max = 330.dp)
    ) {
      Box(
        modifier = Modifier
          .border(
            width = 1.dp,
            brush = Brush.horizontalGradient(
              colors = listOf(
                CyanNeon.copy(alpha = 0.45f),
                VioletNeon.copy(alpha = 0.25f)
              )
            ),
            shape = RoundedCornerShape(
              topStart = 4.dp,
              topEnd = 18.dp,
              bottomStart = 18.dp,
              bottomEnd = 18.dp
            )
          )
          .clip(
            RoundedCornerShape(
              topStart = 4.dp,
              topEnd = 18.dp,
              bottomStart = 18.dp,
              bottomEnd = 18.dp
            )
          )
          .background(NavyCard)
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Column {
          FormattedAiText(text = message.content)
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 4.dp)
      ) {
        Text(
          text = "Nexora • $timeString",
          fontSize = 11.sp,
          color = CyanBright.copy(alpha = 0.75f)
        )

        IconButton(
          onClick = onCopy,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy message",
            tint = TextSecondary,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun ErrorMessageBubble(
  message: ChatMessage,
  onRetry: () -> Unit,
  onOpenSetup: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isKeyIssue = message.errorMessage?.contains("API key", ignoreCase = true) == true ||
      message.errorMessage?.contains("Authentication", ignoreCase = true) == true ||
      message.errorMessage?.contains("configure", ignoreCase = true) == true

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("error_message_${message.id}"),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .background(AccentPink.copy(alpha = 0.2f), CircleShape)
        .border(1.dp, AccentPink.copy(alpha = 0.6f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.ErrorOutline,
        contentDescription = "Error",
        tint = AccentPink,
        modifier = Modifier.size(18.dp)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(
      modifier = Modifier.widthIn(max = 320.dp)
    ) {
      Box(
        modifier = Modifier
          .border(
            width = 1.dp,
            color = AccentPink.copy(alpha = 0.5f),
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
          )
          .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
          .background(Color(0xFF200F1B))
          .padding(14.dp)
      ) {
        Column {
          Text(
            text = "Intelligence Connection Issue",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = AccentPink
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = message.errorMessage ?: "Failed to generate response.",
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = TextPrimary
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            FilledTonalButton(
              onClick = onRetry,
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = AccentPink.copy(alpha = 0.25f),
                contentColor = Color.White
              ),
              modifier = Modifier.height(34.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Retry", fontSize = 12.sp)
            }

            if (isKeyIssue) {
              OutlinedButton(
                onClick = onOpenSetup,
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = CyanNeon
                ),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  Brush.horizontalGradient(listOf(CyanNeon, VioletNeon))
                ),
                modifier = Modifier.height(34.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Settings,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Setup Guide", fontSize = 12.sp)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SystemMessageBubble(
  message: ChatMessage,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = message.content,
      fontSize = 12.sp,
      color = TextSecondary,
      style = MaterialTheme.typography.bodySmall
    )
  }
}

@Composable
private fun FormattedAiText(text: String) {
  val context = LocalContext.current
  // Parse code blocks vs regular paragraphs
  val codeBlockRegex = Regex("```(?:(\\w+)?\\n)?([\\s\\S]*?)```")

  val parts = remember(text) {
    val result = mutableListOf<TextPart>()
    var lastIndex = 0

    for (match in codeBlockRegex.findAll(text)) {
      val matchRange = match.range
      if (matchRange.first > lastIndex) {
        val normalText = text.substring(lastIndex, matchRange.first)
        result.add(TextPart.Normal(normalText))
      }
      val lang = match.groups[1]?.value ?: ""
      val code = match.groups[2]?.value?.trimEnd() ?: ""
      result.add(TextPart.Code(language = lang, code = code))
      lastIndex = matchRange.last + 1
    }

    if (lastIndex < text.length) {
      result.add(TextPart.Normal(text.substring(lastIndex)))
    }

    if (result.isEmpty()) {
      result.add(TextPart.Normal(text))
    }
    result
  }

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    for (part in parts) {
      when (part) {
        is TextPart.Normal -> {
          val annotated = remember(part.content) {
            parseMarkdown(part.content)
          }
          Text(
            text = annotated,
            color = TextPrimary,
            fontSize = 15.sp,
            lineHeight = 22.sp
          )
        }
        is TextPart.Code -> {
          CodeBlockView(
            language = part.language,
            code = part.code,
            onCopy = {
              copyToClipboard(context, part.code)
            }
          )
        }
      }
    }
  }
}

private sealed class TextPart {
  data class Normal(val content: String) : TextPart()
  data class Code(val language: String, val code: String) : TextPart()
}

@Composable
private fun CodeBlockView(
  language: String,
  code: String,
  onCopy: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(NavyDarkest)
      .border(1.dp, NavyBorder, RoundedCornerShape(8.dp))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(NavySurfaceVariant)
        .padding(horizontal = 10.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (language.isNotBlank()) language.uppercase() else "CODE",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = CyanBright
      )
      Row(
        modifier = Modifier
          .clickable { onCopy() }
          .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.ContentCopy,
          contentDescription = "Copy code",
          tint = TextSecondary,
          modifier = Modifier.size(12.dp)
        )
        Text(
          text = "Copy",
          fontSize = 10.sp,
          color = TextSecondary
        )
      }
    }

    Text(
      text = code,
      fontFamily = FontFamily.Monospace,
      fontSize = 13.sp,
      color = Color(0xFFE2E8F0),
      lineHeight = 18.sp,
      modifier = Modifier.padding(10.dp)
    )
  }
}

private fun parseMarkdown(raw: String): androidx.compose.ui.text.AnnotatedString {
  val lines = raw.split("\n")
  return buildAnnotatedString {
    for ((index, line) in lines.withIndex()) {
      val trimmed = line.trimStart()
      when {
        trimmed.startsWith("# ") -> {
          withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CyanBright)) {
            append(trimmed.removePrefix("# "))
          }
        }
        trimmed.startsWith("## ") -> {
          withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CyanBright)) {
            append(trimmed.removePrefix("## "))
          }
        }
        trimmed.startsWith("### ") -> {
          withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = VioletBright)) {
            append(trimmed.removePrefix("### "))
          }
        }
        trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
          withStyle(SpanStyle(color = CyanNeon)) {
            append("• ")
          }
          appendInlineFormatting(trimmed.substring(2))
        }
        else -> {
          appendInlineFormatting(line)
        }
      }
      if (index < lines.size - 1) {
        append("\n")
      }
    }
  }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.appendInlineFormatting(text: String) {
  // Regex for bold **bold** and inline `code`
  val tokenRegex = Regex("(\\*\\*.*?\\*\\*|`.*?`)")
  var cursor = 0

  for (match in tokenRegex.findAll(text)) {
    if (match.range.first > cursor) {
      append(text.substring(cursor, match.range.first))
    }
    val token = match.value
    if (token.startsWith("**") && token.endsWith("**") && token.length >= 4) {
      withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextPrimary)) {
        append(token.substring(2, token.length - 2))
      }
    } else if (token.startsWith("`") && token.endsWith("`") && token.length >= 2) {
      withStyle(SpanStyle(fontFamily = FontFamily.Monospace, color = CyanNeon, background = NavyDarkest)) {
        append(" ${token.substring(1, token.length - 1)} ")
      }
    } else {
      append(token)
    }
    cursor = match.range.last + 1
  }

  if (cursor < text.length) {
    append(text.substring(cursor))
  }
}

private fun copyToClipboard(context: Context, text: String) {
  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val clip = ClipData.newPlainText("Nexora AI Message", text)
  clipboard.setPrimaryClip(clip)
}
