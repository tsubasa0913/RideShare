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

    LaunchedEffect(Unit) {
        rideListViewModel.loadRides()
    }

    LaunchedEffect(uiState.infoMessage) {
        val message = uiState.infoMessage
        if (message != null) {
            scope.launch {
                snackbarHostState.showSnackbar(message)
                rideListViewModel.clearInfoMessage()
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
                        // ▼▼▼ rideIdだけでなくrideオブジェクト全体を渡すように変更 ▼▼▼
                        onRequestClick = { ride ->
                            rideListViewModel.sendRideRequest(ride)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RideList(rides: List<RideOffer>, onRequestClick: (RideOffer) -> Unit) { // ◀◀ 引数の型を変更
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rides) { ride ->
            RideListItem(
                ride = ride,
                onClick = { onRequestClick(ride) } // ◀◀ rideオブジェクト全体を渡す
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideListItem(ride: RideOffer, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "出発地: ${ride.departure}", style = MaterialTheme.typography.titleMedium)
            Text(text = "目的地: ${ride.destination}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "空席: ${ride.availableSeats}席", style = MaterialTheme.typography.bodySmall)
        }
    }
}

