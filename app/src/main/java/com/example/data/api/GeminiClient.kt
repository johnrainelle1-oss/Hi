package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class GeminiResult {
  data class Success(val text: String) : GeminiResult()
  data class Error(val message: String, val isKeyMissing: Boolean = false) : GeminiResult()
}

class GeminiClient(
  private val customApiKeyProvider: () -> String? = { null }
) {
  companion object {
    private const val TAG = "GeminiClient"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    private const val SYSTEM_INSTRUCTION =
      "You are Nexora AI, a futuristic, intelligent, and articulate AI chatbot. " +
      "Your tagline is: 'Your intelligence. Your future.' " +
      "You deliver high-precision insights, helpful answers, and creative reasoning. " +
      "Format your responses cleanly using markdown (bullet points, bold highlights, code blocks with languages where applicable). " +
      "Maintain a professional, forward-thinking, and empowering tone."
  }

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  fun getEffectiveApiKey(): String {
    val customKey = customApiKeyProvider()?.trim()
    if (!customKey.isNullOrBlank()) return customKey

    return try {
      BuildConfig.GEMINI_API_KEY.trim()
    } catch (e: Exception) {
      ""
    }
  }

  fun isApiKeyConfigured(): Boolean {
    val key = getEffectiveApiKey()
    return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
  }

  suspend fun generateResponse(
    conversationHistory: List<ChatMessage>,
    newPrompt: String
  ): GeminiResult = withContext(Dispatchers.IO) {
    val apiKey = getEffectiveApiKey()

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext GeminiResult.Error(
        message = "Gemini API key is not configured. To enable live AI intelligence, configure GEMINI_API_KEY in the AI Studio Secrets panel or enter your key in Settings.",
        isKeyMissing = true
      )
    }

    val url = "$BASE_URL/$MODEL:generateContent?key=$apiKey"

    try {
      val requestBodyJson = buildRequestBody(conversationHistory, newPrompt)
      val body = requestBodyJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

      val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

      val response = client.newCall(request).execute()
      val responseString = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        val errorMessage = parseErrorMessage(response.code, responseString)
        Log.e(TAG, "API Error (${response.code}): $errorMessage")
        return@withContext GeminiResult.Error(
          message = errorMessage,
          isKeyMissing = response.code == 400 || response.code == 403
        )
      }

      val replyText = parseSuccessResponse(responseString)
      if (replyText.isBlank()) {
        return@withContext GeminiResult.Error("Received empty response from Nexora intelligence network.")
      }

      GeminiResult.Success(replyText)
    } catch (e: IOException) {
      Log.e(TAG, "Network exception calling Gemini API", e)
      GeminiResult.Error("Network connection error: ${e.localizedMessage ?: "Unable to connect to Nexora network. Check internet connectivity."}")
    } catch (e: Exception) {
      Log.e(TAG, "Unexpected error in GeminiClient", e)
      GeminiResult.Error("Unexpected error: ${e.localizedMessage ?: "Unknown failure"}")
    }
  }

  private fun buildRequestBody(history: List<ChatMessage>, newPrompt: String): JSONObject {
    val root = JSONObject()

    // System instruction
    val systemInstructionObj = JSONObject()
    val sysParts = JSONArray()
    sysParts.put(JSONObject().put("text", SYSTEM_INSTRUCTION))
    systemInstructionObj.put("parts", sysParts)
    root.put("systemInstruction", systemInstructionObj)

    // Contents array (recent turns for context)
    val contents = JSONArray()

    // Include up to last 10 messages for rich conversation memory
    val recentHistory = history
      .filter { it.status == com.example.data.model.MessageStatus.SENT && it.content.isNotBlank() }
      .takeLast(10)

    for (msg in recentHistory) {
      val contentItem = JSONObject()
      contentItem.put("role", if (msg.sender == MessageSender.USER) "user" else "model")
      val parts = JSONArray()
      parts.put(JSONObject().put("text", msg.content))
      contentItem.put("parts", parts)
      contents.put(contentItem)
    }

    // Add current user prompt
    val currentTurn = JSONObject()
    currentTurn.put("role", "user")
    val curParts = JSONArray()
    curParts.put(JSONObject().put("text", newPrompt))
    currentTurn.put("parts", curParts)
    contents.put(currentTurn)

    root.put("contents", contents)

    // Generation config
    val genConfig = JSONObject()
    genConfig.put("temperature", 0.7)
    genConfig.put("maxOutputTokens", 2048)
    root.put("generationConfig", genConfig)

    return root
  }

  private fun parseSuccessResponse(jsonString: String): String {
    return try {
      val root = JSONObject(jsonString)
      val candidates = root.optJSONArray("candidates") ?: return ""
      if (candidates.length() == 0) return ""

      val firstCandidate = candidates.getJSONObject(0)
      val content = firstCandidate.optJSONObject("content") ?: return ""
      val parts = content.optJSONArray("parts") ?: return ""

      val sb = StringBuilder()
      for (i in 0 until parts.length()) {
        val part = parts.getJSONObject(i)
        sb.append(part.optString("text", ""))
      }
      sb.toString().trim()
    } catch (e: Exception) {
      Log.e(TAG, "Failed to parse Gemini success response", e)
      ""
    }
  }

  private fun parseErrorMessage(statusCode: Int, responseString: String): String {
    try {
      if (responseString.isNotBlank()) {
        val root = JSONObject(responseString)
        val errorObj = root.optJSONObject("error")
        if (errorObj != null) {
          val message = errorObj.optString("message", "")
          val status = errorObj.optString("status", "")
          if (message.isNotBlank()) {
            return when {
              statusCode == 429 -> "Rate limit reached: $message"
              statusCode == 400 || statusCode == 403 -> "Invalid or restricted API Key: $message"
              else -> "Gemini API Error ($status): $message"
            }
          }
        }
      }
    } catch (_: Exception) {}

    return when (statusCode) {
      400 -> "Bad request. Please verify the Gemini model and parameters."
      401, 403 -> "Authentication failed. The provided API key is invalid or lacks Gemini API permissions."
      429 -> "Daily or per-minute quota exceeded on your Gemini API key. Please wait a moment."
      500, 503 -> "Google Gemini service is momentarily unavailable. Please try again shortly."
      else -> "AI service responded with HTTP error code $statusCode."
    }
  }
}
