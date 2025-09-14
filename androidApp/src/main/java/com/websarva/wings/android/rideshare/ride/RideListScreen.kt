package com.websarva.wings.android.rideshare.ride

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.websarva.wings.android.rideshare.shared.data.model.RideOffer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideListScreen(
    rideListViewModel: RideListViewModel = viewModel()
) {
    val uiState by rideListViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ViewModelのメッセージをSnackbarで表示する
    LaunchedEffect(uiState.infoMessage) {
        // infoMessageをローカル変数にコピーしてnull安全性を保証する
        val message = uiState.infoMessage
        if (message != null) {
            scope.launch {
                snackbarHostState.showSnackbar(message)
                rideListViewModel.clearInfoMessage() // メッセージを表示したらクリアする
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.errorMessage != null -> {
                    Text(text = uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    RideList(
                        rides = uiState.rides,
                        onRequestClick = { rideId ->
                            rideListViewModel.sendRideRequest(rideId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RideList(rides: List<RideOffer>, onRequestClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rides) { ride ->
            RideListItem(
                ride = ride,
                onClick = { onRequestClick(ride.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideListItem(ride: RideOffer, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick // カードをタップ可能にする
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "出発地: ${ride.departure}", style = MaterialTheme.typography.titleMedium)
            Text(text = "目的地: ${ride.destination}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "空席: ${ride.availableSeats}席", style = MaterialTheme.typography.bodySmall)
        }
    }
}

