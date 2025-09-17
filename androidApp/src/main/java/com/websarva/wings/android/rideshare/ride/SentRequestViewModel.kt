package com.websarva.wings.android.rideshare.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.model.RideRequest
import com.websarva.wings.android.rideshare.shared.data.ride.RideRepository
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SentRequestUiState(
    val isLoading: Boolean = true,
    val requests: List<RideRequest> = emptyList(),
    val errorMessage: String? = null
)

class SentRequestViewModel : ViewModel() {
    private val rideRepository = RideRepository()

    private val _uiState = MutableStateFlow(SentRequestUiState())
    val uiState: StateFlow<SentRequestUiState> = _uiState.asStateFlow()

    init {
        loadSentRequests()
    }

    private fun loadSentRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val passengerId = UserSession.getCurrentUserId()
            if (passengerId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "ログインしていません。") }
                return@launch
            }

            val result = rideRepository.getSentRequests(passengerId)
            result.onSuccess { requests ->
                _uiState.update { it.copy(isLoading = false, requests = requests) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "リクエストの取得に失敗: ${e.message}") }
            }
        }
    }
}
