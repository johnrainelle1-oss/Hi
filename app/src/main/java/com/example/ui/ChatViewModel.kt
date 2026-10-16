package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiResult
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.DailyQuotaState
import com.example.data.model.MessageSender
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ApiKeyStatus(
  val isConfigured: Boolean,
  val keyPreview: String
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = ChatRepository(application.applicationContext)

  val quotaState: StateFlow<DailyQuotaState> = repository.quotaState
  val sessions: StateFlow<List<ChatSession>> = repository.sessions
  val currentSessionId: StateFlow<String> = repository.currentSessionId
  val messages: StateFlow<List<ChatMessage>> = repository.currentMessages

  private val _inputPrompt = MutableStateFlow("")
  val inputPrompt: StateFlow<String> = _inputPrompt.asStateFlow()

  private val _isGenerating = MutableStateFlow(false)
  val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

  private val _showUpgradeModal = MutableStateFlow(false)
  val showUpgradeModal: StateFlow<Boolean> = _showUpgradeModal.asStateFlow()

  private val _showSetupGuide = MutableStateFlow(false)
  val showSetupGuide: StateFlow<Boolean> = _showSetupGuide.asStateFlow()

  private val _showHistoryDrawer = MutableStateFlow(false)
  val showHistoryDrawer: StateFlow<Boolean> = _showHistoryDrawer.asStateFlow()

  private val _showImageServiceInfo = MutableStateFlow(false)
  val showImageServiceInfo: StateFlow<Boolean> = _showImageServiceInfo.asStateFlow()

  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  val apiKeyStatus: StateFlow<ApiKeyStatus> = MutableStateFlow(calculateApiKeyStatus())

  private fun calculateApiKeyStatus(): ApiKeyStatus {
    val configured = repository.geminiClient.isApiKeyConfigured()
    val rawKey = repository.geminiClient.getEffectiveApiKey()
    val preview = if (configured && rawKey.length > 8) {
      "${rawKey.take(4)}...${rawKey.takeLast(4)}"
    } else if (configured) {
      "Active"
    } else {
      "Not Configured"
    }
    return ApiKeyStatus(isConfigured = configured, keyPreview = preview)
  }

  fun updateInput(text: String) {
    _inputPrompt.value = text
  }

  fun clearToast() {
    _toastMessage.value = null
  }

  fun showToast(msg: String) {
    _toastMessage.value = msg
  }

  fun setUpgradeModalVisible(visible: Boolean) {
    _showUpgradeModal.value = visible
  }

  fun setSetupGuideVisible(visible: Boolean) {
    _showSetupGuide.value = visible
  }

  fun setHistoryDrawerVisible(visible: Boolean) {
    _showHistoryDrawer.value = visible
  }

  fun setImageServiceInfoVisible(visible: Boolean) {
    _showImageServiceInfo.value = visible
  }

  fun sendMessage(presetPrompt: String? = null) {
    val textToSend = (presetPrompt ?: _inputPrompt.value).trim()
    if (textToSend.isBlank() || _isGenerating.value) return

    // Verify daily limit
    if (!repository.canSendMessage()) {
      _showUpgradeModal.value = true
      _toastMessage.value = "Daily free quota reached (50/50 messages)."
      return
    }

    _inputPrompt.value = ""
    _isGenerating.value = true

    // Add User Message
    val userMsg = repository.addUserMessage(textToSend)

    // Add AI placeholder message
    val aiPlaceholderId = repository.addAiMessagePlaceholder()

    viewModelScope.launch {
      val currentHistory = messages.value.filter { it.id != aiPlaceholderId && it.id != userMsg.id }
      val result = repository.geminiClient.generateResponse(currentHistory, textToSend)

      when (result) {
        is GeminiResult.Success -> {
          repository.updateAiMessageSuccess(aiPlaceholderId, result.text)
        }
        is GeminiResult.Error -> {
          repository.updateAiMessageError(aiPlaceholderId, result.message)
          if (result.isKeyMissing) {
            _showSetupGuide.value = true
          }
        }
      }
      _isGenerating.value = false
    }
  }

  fun retryMessage(failedMessageId: String) {
    if (_isGenerating.value) return

    // Find the corresponding user prompt before this failed message
    val list = messages.value
    val failedIndex = list.indexOfFirst { it.id == failedMessageId }
    if (failedIndex == -1) return

    val prevUserMsg = list.take(failedIndex).lastOrNull { it.sender == MessageSender.USER }
    val promptToRetry = prevUserMsg?.content ?: return

    _isGenerating.value = true
    repository.updateAiMessageSuccess(failedMessageId, "") // clear error
    // change status to sending
    val updatedList = messages.value.map {
      if (it.id == failedMessageId) it.copy(status = com.example.data.model.MessageStatus.SENDING, errorMessage = null) else it
    }

    viewModelScope.launch {
      val currentHistory = list.take(failedIndex - 1)
      val result = repository.geminiClient.generateResponse(currentHistory, promptToRetry)
      when (result) {
        is GeminiResult.Success -> {
          repository.updateAiMessageSuccess(failedMessageId, result.text)
        }
        is GeminiResult.Error -> {
          repository.updateAiMessageError(failedMessageId, result.message)
          if (result.isKeyMissing) {
            _showSetupGuide.value = true
          }
        }
      }
      _isGenerating.value = false
    }
  }

  fun startNewChat() {
    repository.createNewSession("New Chat")
    _showHistoryDrawer.value = false
  }

  fun selectSession(sessionId: String) {
    repository.selectSession(sessionId)
    _showHistoryDrawer.value = false
  }

  fun deleteSession(sessionId: String) {
    repository.deleteSession(sessionId)
  }

  fun clearCurrentChat() {
    repository.clearCurrentChat()
  }

  fun toggleProDemoMode(enabled: Boolean) {
    repository.setDemoProMode(enabled)
    val msg = if (enabled) {
      "Nexora Pro mode activated (Demo: Unlimited messages)"
    } else {
      "Reverted to Free Tier (50 daily messages limit)"
    }
    _toastMessage.value = msg
  }

  fun saveCustomApiKey(key: String) {
    repository.saveCustomApiKey(key)
    (apiKeyStatus as MutableStateFlow).value = calculateApiKeyStatus()
    _toastMessage.value = if (key.isBlank()) "Custom API Key cleared" else "API Key saved successfully"
  }

  fun resetDailyQuota() {
    repository.resetDailyQuotaForTesting()
    _toastMessage.value = "Daily quota reset to 0/50 used"
  }
}
