package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.GeminiClient
import com.example.data.api.GeminiResult
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.DailyQuotaState
import com.example.data.model.MessageSender
import com.example.data.model.MessageStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatRepository(private val context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("nexora_ai_prefs", Context.MODE_PRIVATE)

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

  private val _quotaState = MutableStateFlow(loadDailyQuota())
  val quotaState: StateFlow<DailyQuotaState> = _quotaState.asStateFlow()

  private val _sessions = MutableStateFlow<List<ChatSession>>(emptyList())
  val sessions: StateFlow<List<ChatSession>> = _sessions.asStateFlow()

  private val _currentSessionId = MutableStateFlow<String>("")
  val currentSessionId: StateFlow<String> = _currentSessionId.asStateFlow()

  private val _currentMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
  val currentMessages: StateFlow<List<ChatMessage>> = _currentMessages.asStateFlow()

  val geminiClient: GeminiClient = GeminiClient {
    getCustomApiKey()
  }

  init {
    loadSessions()
    ensureActiveSession()
  }

  private fun getTodayDateKey(): String {
    return dateFormat.format(Date())
  }

  private fun loadDailyQuota(): DailyQuotaState {
    val today = getTodayDateKey()
    val savedDate = prefs.getString("quota_date", "") ?: ""
    val isPro = prefs.getBoolean("is_demo_pro_mode", false)

    val count = if (savedDate == today) {
      prefs.getInt("quota_count", 0)
    } else {
      // New day -> reset to 0
      prefs.edit()
        .putString("quota_date", today)
        .putInt("quota_count", 0)
        .apply()
      0
    }

    return DailyQuotaState(
      usedToday = count,
      dailyLimit = 50,
      isProMode = isPro,
      dateKey = today
    )
  }

  fun canSendMessage(): Boolean {
    val currentQuota = loadDailyQuota()
    _quotaState.value = currentQuota
    return !currentQuota.isExhausted
  }

  fun incrementMessageUsage() {
    val today = getTodayDateKey()
    val isPro = prefs.getBoolean("is_demo_pro_mode", false)
    val savedDate = prefs.getString("quota_date", "") ?: ""
    val currentCount = if (savedDate == today) prefs.getInt("quota_count", 0) else 0
    val newCount = currentCount + 1

    prefs.edit()
      .putString("quota_date", today)
      .putInt("quota_count", newCount)
      .apply()

    _quotaState.value = DailyQuotaState(
      usedToday = newCount,
      dailyLimit = 50,
      isProMode = isPro,
      dateKey = today
    )
  }

  fun setDemoProMode(enabled: Boolean) {
    prefs.edit().putBoolean("is_demo_pro_mode", enabled).apply()
    _quotaState.value = _quotaState.value.copy(isProMode = enabled)
  }

  fun saveCustomApiKey(key: String) {
    prefs.edit().putString("custom_api_key", key.trim()).apply()
  }

  fun getCustomApiKey(): String? {
    val key = prefs.getString("custom_api_key", null)?.trim()
    return if (key.isNullOrBlank()) null else key
  }

  fun resetDailyQuotaForTesting() {
    val today = getTodayDateKey()
    prefs.edit()
      .putString("quota_date", today)
      .putInt("quota_count", 0)
      .apply()
    _quotaState.value = _quotaState.value.copy(usedToday = 0)
  }

  // --- Session Management ---

  private fun loadSessions() {
    val rawJson = prefs.getString("saved_sessions", null)
    if (rawJson.isNullOrBlank()) {
      _sessions.value = emptyList()
      return
    }

    try {
      val jsonArray = JSONArray(rawJson)
      val list = mutableListOf<ChatSession>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          ChatSession(
            id = obj.getString("id"),
            title = obj.getString("title"),
            createdAt = obj.getLong("createdAt"),
            lastUpdatedAt = obj.getLong("lastUpdatedAt"),
            preview = obj.optString("preview", "")
          )
        )
      }
      _sessions.value = list.sortedByDescending { it.lastUpdatedAt }
    } catch (_: Exception) {
      _sessions.value = emptyList()
    }
  }

  private fun saveSessionsList(sessionsList: List<ChatSession>) {
    try {
      val jsonArray = JSONArray()
      for (s in sessionsList) {
        val obj = JSONObject()
        obj.put("id", s.id)
        obj.put("title", s.title)
        obj.put("createdAt", s.createdAt)
        obj.put("lastUpdatedAt", s.lastUpdatedAt)
        obj.put("preview", s.preview)
        jsonArray.put(obj)
      }
      prefs.edit().putString("saved_sessions", jsonArray.toString()).apply()
      _sessions.value = sessionsList.sortedByDescending { it.lastUpdatedAt }
    } catch (_: Exception) {}
  }

  private fun ensureActiveSession() {
    if (_sessions.value.isNotEmpty()) {
      selectSession(_sessions.value.first().id)
    } else {
      createNewSession("Welcome to Nexora")
    }
  }

  fun createNewSession(title: String = "New Chat"): String {
    val newId = UUID.randomUUID().toString()
    val session = ChatSession(
      id = newId,
      title = title,
      createdAt = System.currentTimeMillis(),
      lastUpdatedAt = System.currentTimeMillis(),
      preview = "Start a conversation..."
    )
    val updated = listOf(session) + _sessions.value
    saveSessionsList(updated)
    selectSession(newId)
    return newId
  }

  fun selectSession(sessionId: String) {
    _currentSessionId.value = sessionId
    loadMessagesForSession(sessionId)
  }

  fun deleteSession(sessionId: String) {
    val updated = _sessions.value.filter { it.id != sessionId }
    saveSessionsList(updated)
    prefs.edit().remove("msgs_$sessionId").apply()

    if (_currentSessionId.value == sessionId) {
      if (updated.isNotEmpty()) {
        selectSession(updated.first().id)
      } else {
        createNewSession("New Chat")
      }
    }
  }

  fun clearCurrentChat() {
    val curId = _currentSessionId.value
    if (curId.isNotBlank()) {
      _currentMessages.value = emptyList()
      prefs.edit().remove("msgs_$curId").apply()
      val updatedSessions = _sessions.value.map {
        if (it.id == curId) it.copy(preview = "Empty conversation", lastUpdatedAt = System.currentTimeMillis()) else it
      }
      saveSessionsList(updatedSessions)
    }
  }

  // --- Messages Management ---

  private fun loadMessagesForSession(sessionId: String) {
    val rawJson = prefs.getString("msgs_$sessionId", null)
    if (rawJson.isNullOrBlank()) {
      _currentMessages.value = emptyList()
      return
    }

    try {
      val jsonArray = JSONArray(rawJson)
      val msgs = mutableListOf<ChatMessage>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        msgs.add(
          ChatMessage(
            id = obj.getString("id"),
            sender = MessageSender.valueOf(obj.getString("sender")),
            content = obj.getString("content"),
            timestamp = obj.getLong("timestamp"),
            status = MessageStatus.valueOf(obj.optString("status", MessageStatus.SENT.name)),
            errorMessage = obj.optString("errorMessage").takeIf { it.isNotBlank() }
          )
        )
      }
      _currentMessages.value = msgs
    } catch (_: Exception) {
      _currentMessages.value = emptyList()
    }
  }

  private fun saveCurrentMessages() {
    val curId = _currentSessionId.value
    if (curId.isBlank()) return

    val msgs = _currentMessages.value
    try {
      val jsonArray = JSONArray()
      for (m in msgs) {
        val obj = JSONObject()
        obj.put("id", m.id)
        obj.put("sender", m.sender.name)
        obj.put("content", m.content)
        obj.put("timestamp", m.timestamp)
        obj.put("status", m.status.name)
        m.errorMessage?.let { obj.put("errorMessage", it) }
        jsonArray.put(obj)
      }
      prefs.edit().putString("msgs_$curId", jsonArray.toString()).apply()

      // Update session title and preview if first message
      val firstUserMsg = msgs.firstOrNull { it.sender == MessageSender.USER }
      val lastMsg = msgs.lastOrNull()
      val updatedSessions = _sessions.value.map { session ->
        if (session.id == curId) {
          val newTitle = if (session.title == "New Chat" || session.title == "Welcome to Nexora") {
            firstUserMsg?.content?.take(26)?.trim() ?: session.title
          } else {
            session.title
          }
          session.copy(
            title = newTitle,
            lastUpdatedAt = System.currentTimeMillis(),
            preview = lastMsg?.content?.take(40)?.replace("\n", " ") ?: ""
          )
        } else session
      }
      saveSessionsList(updatedSessions)
    } catch (_: Exception) {}
  }

  fun addUserMessage(text: String): ChatMessage {
    val message = ChatMessage(
      id = UUID.randomUUID().toString(),
      sender = MessageSender.USER,
      content = text,
      timestamp = System.currentTimeMillis(),
      status = MessageStatus.SENT
    )
    _currentMessages.value = _currentMessages.value + message
    saveCurrentMessages()
    incrementMessageUsage()
    return message
  }

  fun addAiMessagePlaceholder(): String {
    val messageId = UUID.randomUUID().toString()
    val message = ChatMessage(
      id = messageId,
      sender = MessageSender.AI,
      content = "",
      timestamp = System.currentTimeMillis(),
      status = MessageStatus.SENDING
    )
    _currentMessages.value = _currentMessages.value + message
    return messageId
  }

  fun updateAiMessageSuccess(messageId: String, content: String) {
    _currentMessages.value = _currentMessages.value.map {
      if (it.id == messageId) {
        it.copy(
          content = content,
          status = MessageStatus.SENT,
          errorMessage = null
        )
      } else it
    }
    saveCurrentMessages()
  }

  fun updateAiMessageError(messageId: String, errorMessage: String) {
    _currentMessages.value = _currentMessages.value.map {
      if (it.id == messageId) {
        it.copy(
          status = MessageStatus.ERROR,
          errorMessage = errorMessage
        )
      } else it
    }
    saveCurrentMessages()
  }
}
