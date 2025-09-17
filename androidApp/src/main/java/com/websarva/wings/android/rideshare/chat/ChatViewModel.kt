package com.websarva.wings.android.rideshare.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.chat.ChatRepository
import com.websarva.wings.android.rideshare.shared.data.model.Message
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = true
)

// ▼▼▼ 1. rideRequestId をコンストラクタで受け取るように変更 ▼▼▼
class ChatViewModel(private val rideRequestId: String) : ViewModel() {
    private val chatRepository = ChatRepository()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        listenForMessages()
    }

    private fun listenForMessages() {
        viewModelScope.launch {
            // ▼▼▼ 2. 受け取った rideRequestId を使用 ▼▼▼
            chatRepository.getMessages(rideRequestId).collect { messages ->
                _uiState.value = ChatUiState(messages = messages, isLoading = false)
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            // ▼▼▼ 3. テスト用の仮IDを削除し、UserSessionを再度使用 ▼▼▼
            val senderId = UserSession.getCurrentUserId() ?: return@launch
            if (text.isBlank()) return@launch

            val message = Message(
                text = text,
                senderId = senderId,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
            // ▼▼▼ 4. 受け取った rideRequestId を使用 ▼▼▼
            chatRepository.sendMessage(rideRequestId, message)
        }
    }
}

