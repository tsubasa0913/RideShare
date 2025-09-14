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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.websarva.wings.android.rideshare.shared.data.model.RideRequest
import com.websarva.wings.android.rideshare.shared.data.model.RequestStatus

@Composable
fun RequestManagementScreen(
    viewModel: RequestManagementViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.errorMessage != null -> Text(uiState.errorMessage!!)
            uiState.requests.isEmpty() -> Text("受信したリクエストはありません。")
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.requests) { request ->
                        RequestItem(
                            request = request,
                            onApprove = { viewModel.approveRequest(request.id) },
                            onReject = { viewModel.rejectRequest(request.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequestItem(
    request: RideRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("乗客ID: ${request.passengerId}", style = MaterialTheme.typography.titleMedium)
            Text("乗車ID: ${request.rideOfferId}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            RequestStatusBadge(status = request.status)
            Spacer(Modifier.height(12.dp))

            if (request.status == RequestStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onApprove, modifier = Modifier.weight(1f)) {
                        Text("承認")
                    }
                    OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f)) {
                        Text("却下")
                    }
                }
            }
        }
    }
}

@Composable
fun RequestStatusBadge(status: RequestStatus) {
    val (text, color) = when (status) {
        RequestStatus.PENDING -> "承認待ち" to Color.Gray
        RequestStatus.ACCEPTED -> "承認済み" to Color(0xFF4CAF50) // Green
        RequestStatus.REJECTED -> "却下済み" to Color.Red
    }
    Text(text, color = color, style = MaterialTheme.typography.labelLarge)
}
