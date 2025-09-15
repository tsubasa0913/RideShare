package com.websarva.wings.android.rideshare.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.websarva.wings.android.rideshare.shared.data.model.Message
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel()
) {
    val uiState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 新しいメッセージが来たら一番下までスクロールする
    LaunchedEffect(uiState.messages) {
        if (uiState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.messages.lastIndex)
            }
        }
    }

    Scaffold(
        bottomBar = {
            MessageInput(onSend = { text -> chatViewModel.sendMessage(text) })
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages) { message ->
                MessageBubble(message = message)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val isMyMessage = message.senderId == UserSession.getCurrentUserId()
    val alignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (isMyMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Text(
            text = message.text,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("メッセージを入力") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(
                onClick = {
                    onSend(text)
                    text = "" // 送信したら入力欄をクリア
                },
                enabled = text.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = "送信")
            }
        }
    }
}
