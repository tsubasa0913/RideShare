package com.websarva.wings.android.rideshare.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.ride.PostRideRequest
import com.websarva.wings.android.rideshare.shared.data.ride.RideRepository
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

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

            // 1. UserSessionから現在ログインしているユーザーのIDを取得
            val currentUserId = UserSession.getCurrentUserId()
            if (currentUserId == null) {
                // ログインしていない場合はエラーメッセージを表示して処理を中断
                _uiState.value = RidePostUiState(errorMessage = "ログインしていません。")
                return@launch
            }

            val request = PostRideRequest(
                // 2. driverIdに取得したユーザーIDを設定
                driverId = currentUserId,
                departure = departure,
                destination = destination,
                // 3. 時刻の取得方法をマルチプラットフォーム対応のものに変更
                departureTime = Clock.System.now().toEpochMilliseconds(),
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

