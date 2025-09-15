package com.websarva.wings.android.rideshare.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.ride.PostRideRequest
import com.websarva.wings.android.rideshare.shared.data.ride.RideRepository
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

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
            _uiState.update { it.copy(isLoading = true) }
            val currentUserId = UserSession.getCurrentUserId()
            if (currentUserId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "ログインしていません。") }
                return@launch
            }

            val request = PostRideRequest(
                driverId = currentUserId,
                departure = departure,
                destination = destination,
                departureTime = Clock.System.now().toEpochMilliseconds(),
                availableSeats = availableSeats.toIntOrNull() ?: 0,
                description = description
            )

            rideRepository.postRide(request).onSuccess {
                _uiState.update { it.copy(isLoading = false, postSuccess = true) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "投稿に失敗しました: ${exception.message}") }
            }
        }
    }

    /**
     * 画面遷移など、成功イベントが処理された後に呼び出す関数
     */
    fun onPostSuccessHandled() {
        _uiState.update { it.copy(postSuccess = false) }
    }
}

