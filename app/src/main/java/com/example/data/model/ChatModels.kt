package com.example.data.model

enum class MessageSender {
  USER,
  AI,
  SYSTEM
}

enum class MessageStatus {
  SENT,
  SENDING,
  ERROR
}

data class ChatMessage(
  val id: String,
  val sender: MessageSender,
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val status: MessageStatus = MessageStatus.SENT,
  val errorMessage: String? = null
)

data class ChatSession(
  val id: String,
  val title: String,
  val createdAt: Long = System.currentTimeMillis(),
  val lastUpdatedAt: Long = System.currentTimeMillis(),
  val preview: String = ""
)

enum class PlanTier(val title: String, val dailyTextLimit: Int) {
  FREE("Free Tier", 50),
  PRO("Nexora Pro", 10000),
  ULTRA("Nexora Ultra", 50000)
}

data class DailyQuotaState(
  val usedToday: Int = 0,
  val dailyLimit: Int = 50,
  val isProMode: Boolean = false,
  val dateKey: String = ""
) {
  val remainingToday: Int
    get() = if (isProMode) 9999 else (dailyLimit - usedToday).coerceAtLeast(0)

  val isExhausted: Boolean
    get() = !isProMode && usedToday >= dailyLimit
}
