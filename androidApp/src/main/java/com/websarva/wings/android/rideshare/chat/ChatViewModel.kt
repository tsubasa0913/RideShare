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

class ChatViewModel : ViewModel() {
    private val chatRepository = ChatRepository()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // 仮のチャットルームID。本来はリクエスト一覧などから渡される
    private val rideRequestId = "dummy-request-id" // TODO: 動的に設定する

    init {
        listenForMessages()
    }

    private fun listenForMessages() {
        viewModelScope.launch {
            chatRepository.getMessages(rideRequestId).collect { messages ->
                _uiState.value = ChatUiState(messages = messages, isLoading = false)
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            // val senderId = UserSession.getCurrentUserId() ?: return@launch
            val senderId = "my-test-user-id" // ◀◀◀ 仮のIDを設定
            if (text.isBlank()) return@launch

            val message = Message(
                text = text,
                senderId = senderId,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
            chatRepository.sendMessage(rideRequestId, message)
            // 送信後の処理（成功・失敗のフィードバックなど）はここでは省略
        }
    }
}
