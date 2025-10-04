package com.websarva.wings.android.rideshare.ride

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
                        posts = uiState.posts, // ◀◀ ridesからpostsに変更
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
fun RideList(
    posts: List<RidePostDisplayData>, // ◀◀ 型を変更
    onRequestClick: (RideOffer) -> Unit
) {
    if (posts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("現在、利用可能な乗車情報はありません。")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts) { post -> // ◀◀ 変数名をpostに変更
                RideListItem(
                    post = post, // ◀◀ postを渡す
                    onClick = { onRequestClick(post.ride) } // ◀◀ rideだけを渡す
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideListItem(
    post: RidePostDisplayData, // ◀◀ 型を変更
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ▼▼▼ 投稿者名を表示するTextを追加 ▼▼▼
            Text(
                text = post.driverName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "出発地: ${post.ride.departure}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "目的地: ${post.ride.destination}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "空席: ${post.ride.availableSeats}席", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

