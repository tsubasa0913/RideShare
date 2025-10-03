package com.websarva.wings.android.rideshare.ride

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.websarva.wings.android.rideshare.shared.data.model.RideRequest
import com.websarva.wings.android.rideshare.shared.data.model.RequestStatus

@Composable
fun SentRequestScreen(
    // ▼▼▼ 1. チャットを開くためのコールバックを追加 ▼▼▼
    onOpenChat: (requestId: String) -> Unit,
    viewModel: SentRequestViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.errorMessage != null -> Text(uiState.errorMessage!!)
            uiState.requests.isEmpty() -> Text("送信したリクエストはありません。")
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.requests) { request ->
                        SentRequestItem(
                            request = request,
                            // ▼▼▼ 2. onOpenChatを下に渡す ▼▼▼
                            onOpenChat = { onOpenChat(request.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentRequestItem(
    request: RideRequest,
    // ▼▼▼ 3. onOpenChatを受け取る ▼▼▼
    onOpenChat: () -> Unit
) {
    // ▼▼▼ 4. 承認済みの場合はカード全体をタップ可能にする ▼▼▼
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (request.status == RequestStatus.ACCEPTED) {
                onOpenChat()
            }
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("乗車ID: ${request.rideOfferId}", style = MaterialTheme.typography.titleMedium)
            Text("ドライバーID: ${request.driverId}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            // 共通のRequestStatusBadgeコンポーザブルを再利用
            RequestStatusBadge(status = request.status)
        }
    }
}

