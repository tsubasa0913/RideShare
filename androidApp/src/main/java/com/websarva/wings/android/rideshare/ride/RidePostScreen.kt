package com.websarva.wings.android.rideshare.ride

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RidePostScreen(
    onPostSuccess: () -> Unit,
    ridePostViewModel: RidePostViewModel = viewModel()
) {
    val uiState by ridePostViewModel.uiState.collectAsState()

    // 投稿成功の状態を一度だけ処理する
    if (uiState.postSuccess) {
        LaunchedEffect(uiState.postSuccess) {
            onPostSuccess() // 親に成功を通知して画面遷移をトリガー
            ridePostViewModel.onPostSuccessHandled() // ViewModelの状態をリセット
        }
    }

    var departure by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var departureTime by remember { mutableStateOf("") }
    var availableSeats by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("乗車情報の提供", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = departure,
                onValueChange = { departure = it },
                label = { Text("出発地") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("目的地") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = departureTime,
                onValueChange = { departureTime = it },
                label = { Text("出発日時 (例: 2025/09/15 10:00)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = availableSeats,
                onValueChange = { availableSeats = it },
                label = { Text("空席数") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("補足情報（任意）") },
                modifier = Modifier.fillMaxWidth()
            )

            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    ridePostViewModel.postRide(departure, destination, departureTime, availableSeats, description)
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("投稿する")
                }
            }
        }
    }
}

