package com.websarva.wings.android.rideshare.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.ride.PostRideRequest
import com.websarva.wings.android.rideshare.shared.data.ride.RideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UIの状態を表すデータクラス (変更なし)
data class RidePostUiState(
    val isLoading: Boolean = false,
    val postSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RidePostViewModel : ViewModel() {
    private val rideRepository = RideRepository()

    private val _uiState = MutableStateFlow(RidePostUiState())
    val uiState: StateFlow<RidePostUiState> = _uiState.asStateFlow()

    fun postRide(
        departure: String,
        destination: String,
        departureTime: String,
        availableSeats: String,
        description: String?
    ) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = RidePostUiState(isLoading = true)

            val request = PostRideRequest(
                driverId = "dummy-driver-id-from-firebase", // TODO: ログインユーザーのIDに置き換える
                departure = departure,
                destination = destination,
                departureTime = System.currentTimeMillis(),
                availableSeats = availableSeats.toIntOrNull() ?: 0,
                description = description
            )

            val result = rideRepository.postRide(request)

            // ▼▼▼ RideRepositoryの戻り値が Result<Unit> に変わったため、ここの処理を修正 ▼▼▼
            result.onSuccess {
                // 成功した場合は何も受け取らないので、引数は不要
                _uiState.value = RidePostUiState(postSuccess = true)
            }.onFailure { exception ->
                _uiState.value = RidePostUiState(errorMessage = "投稿に失敗しました: ${exception.message}")
            }
        }
    }
}

