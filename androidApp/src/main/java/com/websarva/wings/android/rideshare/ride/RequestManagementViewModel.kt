package com.websarva.wings.android.rideshare.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.model.RideRequest
import com.websarva.wings.android.rideshare.shared.data.model.RequestStatus
import com.websarva.wings.android.rideshare.shared.data.ride.RideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RequestManagementUiState(
    val isLoading: Boolean = true,
    val requests: List<RideRequest> = emptyList(),
    val errorMessage: String? = null
)

class RequestManagementViewModel : ViewModel() {
    private val rideRepository = RideRepository()

    private val _uiState = MutableStateFlow(RequestManagementUiState())
    val uiState: StateFlow<RequestManagementUiState> = _uiState.asStateFlow()

    init {
        loadReceivedRequests()
    }

    private fun loadReceivedRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: 本来は現在ログインしているドライバーのIDを使う
            val driverId = "dummy-driver-id-from-firebase"
            val result = rideRepository.getReceivedRequests(driverId)
            result.onSuccess { requests ->
                _uiState.update { it.copy(isLoading = false, requests = requests) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "リクエストの取得に失敗: ${e.message}") }
            }
        }
    }

    fun approveRequest(requestId: String) {
        updateRequestStatus(requestId, RequestStatus.ACCEPTED)
    }

    fun rejectRequest(requestId: String) {
        updateRequestStatus(requestId, RequestStatus.REJECTED)
    }

    private fun updateRequestStatus(requestId: String, status: RequestStatus) {
        viewModelScope.launch {
            rideRepository.updateRequestStatus(requestId, status).onSuccess {
                // 成功したらリストを再読み込みして表示を更新
                loadReceivedRequests()
            }
        }
    }
}
