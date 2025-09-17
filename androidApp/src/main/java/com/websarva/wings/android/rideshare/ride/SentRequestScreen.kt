package com.websarva.wings.android.rideshare.ride

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.websarva.wings.android.rideshare.shared.data.model.RideRequest

@Composable
fun SentRequestScreen(
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
                        SentRequestItem(request = request)
                    }
                }
            }
        }
    }
}

@Composable
fun SentRequestItem(request: RideRequest) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("乗車ID: ${request.rideOfferId}", style = MaterialTheme.typography.titleMedium)
            Text("ドライバーID: ${request.driverId}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            // 共通のRequestStatusBadgeコンポーザブルを再利用
            RequestStatusBadge(status = request.status)
        }
    }
}
