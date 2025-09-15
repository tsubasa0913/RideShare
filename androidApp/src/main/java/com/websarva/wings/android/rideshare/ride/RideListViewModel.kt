package com.websarva.wings.android.rideshare.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.model.RideOffer
import com.websarva.wings.android.rideshare.shared.data.ride.RideRepository
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 乗車一覧画面のUIの状態
data class RideListUiState(
    val isLoading: Boolean = true,
    val rides: List<RideOffer> = emptyList(),
    val infoMessage: String? = null,
    val errorMessage: String? = null
)

class RideListViewModel : ViewModel() {
    private val rideRepository = RideRepository()

    private val _uiState = MutableStateFlow(RideListUiState())
    val uiState: StateFlow<RideListUiState> = _uiState.asStateFlow()

    init {
        loadRides()
    }

    fun loadRides() {
        viewModelScope.launch {
            _uiState.value = RideListUiState(isLoading = true)
            val result = rideRepository.getAllRides()
            result.onSuccess { rideList ->
                _uiState.value = RideListUiState(isLoading = false, rides = rideList)
            }.onFailure { exception ->
                _uiState.value = RideListUiState(isLoading = false, errorMessage = "データの取得に失敗しました: ${exception.message}")
            }
        }
    }

    /**
     * 乗車リクエストを送信する
     */
    fun sendRideRequest(rideOfferId: String) {
        viewModelScope.launch {
            // --- ▼▼▼ ここから修正 ▼▼▼ ---

            // 1. UserSessionから現在ログインしているユーザーのIDを取得
            val passengerId = UserSession.getCurrentUserId()
            if (passengerId == null) {
                // ログインしていない場合はエラーメッセージを表示して処理を中断
                _uiState.update { it.copy(errorMessage = "ログインしていません。") }
                return@launch
            }

            // 2. 取得したユーザーIDを使ってリクエストを送信
            val result = rideRepository.sendRideRequest(rideOfferId, passengerId)

            // --- ▲▲▲ ここまで修正 ▲▲▲ ---

            result.onSuccess {
                _uiState.update { it.copy(infoMessage = "乗車リクエストを送信しました。") }
            }.onFailure {
                _uiState.update { it.copy(errorMessage = "リクエストの送信に失敗しました。") }
            }
        }
    }

    /**
     * UIに表示したメッセージを消去する
     */
    fun clearInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }
}

