package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.model.MessageSender
import com.example.ui.ChatViewModel
import com.example.ui.components.ChatInputBar
import com.example.ui.components.EmptyChatView
import com.example.ui.components.MessageItem
import com.example.ui.components.NexoraTopBar
import com.example.ui.components.TypingIndicator
import com.example.ui.theme.NavyBackground

@Composable
fun ChatScreen(
  viewModel: ChatViewModel,
  modifier: Modifier = Modifier
) {
  val messages by viewModel.messages.collectAsState()
  val quotaState by viewModel.quotaState.collectAsState()
  val sessions by viewModel.sessions.collectAsState()
  val currentSessionId by viewModel.currentSessionId.collectAsState()
  val inputPrompt by viewModel.inputPrompt.collectAsState()
  val isGenerating by viewModel.isGenerating.collectAsState()
  val apiKeyStatus by viewModel.apiKeyStatus.collectAsState()

  val showUpgradeModal by viewModel.showUpgradeModal.collectAsState()
  val showSetupGuide by viewModel.showSetupGuide.collectAsState()
  val showHistoryDrawer by viewModel.showHistoryDrawer.collectAsState()
  val showImageServiceInfo by viewModel.showImageServiceInfo.collectAsState()
  val toastMessage by viewModel.toastMessage.collectAsState()

  val listState = rememberLazyListState()
  val snackbarHostState = remember { SnackbarHostState() }

  // Auto scroll to bottom when a new message arrives or generating state changes
  LaunchedEffect(messages.size, isGenerating) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  // Handle toast notifications
  LaunchedEffect(toastMessage) {
    val msg = toastMessage
    if (msg != null) {
      snackbarHostState.showSnackbar(msg)
      viewModel.clearToast()
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
      .testTag("chat_screen"),
    containerColor = NavyBackground,
    topBar = {
      NexoraTopBar(
        quotaState = quotaState,
        onNewChat = { viewModel.startNewChat() },
        onOpenHistory = { viewModel.setHistoryDrawerVisible(true) },
        onOpenUpgrade = { viewModel.setUpgradeModalVisible(true) },
        onOpenSetup = { viewModel.setSetupGuideVisible(true) }
      )
    },
    bottomBar = {
      ChatInputBar(
        text = inputPrompt,
        onTextChange = { viewModel.updateInput(it) },
        onSend = { viewModel.sendMessage() },
        isGenerating = isGenerating,
        quotaState = quotaState,
        onOpenImageService = { viewModel.setImageServiceInfoVisible(true) },
        onOpenUpgrade = { viewModel.setUpgradeModalVisible(true) }
      )
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(NavyBackground)
    ) {
      if (messages.isEmpty()) {
        EmptyChatView(
          quotaState = quotaState,
          onPromptSelected = { prompt ->
            viewModel.sendMessage(presetPrompt = prompt)
          },
          onOpenUpgrade = { viewModel.setUpgradeModalVisible(true) },
          modifier = Modifier.fillMaxSize()
        )
      } else {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .fillMaxSize()
            .testTag("messages_list")
        ) {
          items(messages, key = { it.id }) { message ->
            // If message is AI and is currently generating (empty content), show TypingIndicator
            if (message.sender == MessageSender.AI && message.status == com.example.data.model.MessageStatus.SENDING && message.content.isBlank()) {
              TypingIndicator()
            } else {
              MessageItem(
                message = message,
                onRetry = { viewModel.retryMessage(message.id) },
                onOpenSetup = { viewModel.setSetupGuideVisible(true) },
                onCopied = { msg -> viewModel.showToast(msg) }
              )
            }
          }
        }
      }
    }
  }

  // Modals & Bottom Sheets
  if (showHistoryDrawer) {
    ChatHistorySheet(
      sessions = sessions,
      currentSessionId = currentSessionId,
      onSelectSession = { id -> viewModel.selectSession(id) },
      onDeleteSession = { id -> viewModel.deleteSession(id) },
      onNewChat = { viewModel.startNewChat() },
      onDismiss = { viewModel.setHistoryDrawerVisible(false) }
    )
  }

  if (showUpgradeModal) {
    PremiumUpgradeSheet(
      quotaState = quotaState,
      onToggleDemoPro = { enabled -> viewModel.toggleProDemoMode(enabled) },
      onResetQuota = { viewModel.resetDailyQuota() },
      onJoinWaitlist = { viewModel.showToast("Joined Nexora Pro Waitlist! We'll notify you on launch.") },
      onDismiss = { viewModel.setUpgradeModalVisible(false) }
    )
  }

  if (showImageServiceInfo) {
    ImageServiceDialog(
      isApiKeyConfigured = apiKeyStatus.isConfigured,
      onOpenSetup = { viewModel.setSetupGuideVisible(true) },
      onDismiss = { viewModel.setImageServiceInfoVisible(false) }
    )
  }

  if (showSetupGuide) {
    SetupGuideDialog(
      apiKeyStatus = apiKeyStatus,
      onSaveKey = { key -> viewModel.saveCustomApiKey(key) },
      onDismiss = { viewModel.setSetupGuideVisible(false) }
    )
  }
}
