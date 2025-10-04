package com.websarva.wings.android.rideshare.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.model.RideOffer
import com.websarva.wings.android.rideshare.shared.data.model.User
import com.websarva.wings.android.rideshare.shared.data.ride.RideRepository
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import com.websarva.wings.android.rideshare.shared.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ▼▼▼ UIに表示するための新しいデータクラスを定義 ▼▼▼
data class RidePostDisplayData(
    val ride: RideOffer,
    val driverName: String
)

// 乗車一覧画面のUIの状態
data class RideListUiState(
    val isLoading: Boolean = true,
    val posts: List<RidePostDisplayData> = emptyList(), // ◀◀ RideOfferから変更
    val infoMessage: String? = null,
    val errorMessage: String? = null
)

class RideListViewModel : ViewModel() {
    private val rideRepository = RideRepository()
    private val userRepository = UserRepository() // ◀◀ UserRepositoryを追加

    private val _uiState = MutableStateFlow(RideListUiState())
    val uiState: StateFlow<RideListUiState> = _uiState.asStateFlow()

    fun loadRides() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. まず乗車情報をすべて取得
            val ridesResult = rideRepository.getAllRides()
            if (ridesResult.isFailure) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "データの取得に失敗しました。") }
                return@launch
            }
            val rides = ridesResult.getOrNull() ?: emptyList()

            // 2. 乗車情報から投稿者のIDリストを作成
            val driverIds = rides.map { it.driverId }.distinct()

            // 3. 投稿者のプロフィール情報をまとめて取得
            val usersResult = userRepository.getUsers(driverIds)
            val usersMap = usersResult.getOrNull() ?: emptyMap()

            // 4. 乗車情報と投稿者名を結合したリストを作成
            val displayData = rides.map { ride ->
                RidePostDisplayData(
                    ride = ride,
                    driverName = usersMap[ride.driverId]?.name ?: "不明なユーザー"
                )
            }

            _uiState.update { it.copy(isLoading = false, posts = displayData) }
        }
    }

    /**
     * 乗車リクエストを送信する (driverIdも渡すよう変更)
     */
    fun sendRideRequest(rideOffer: RideOffer) { // ◀◀ rideIdだけでなくRideOffer全体を受け取る
        viewModelScope.launch {
            val passengerId = UserSession.getCurrentUserId()
            if (passengerId == null) {
                _uiState.update { it.copy(errorMessage = "ログインしていません。") }
                return@launch
            }
            // ▼▼▼ rideOfferからIDとdriverIdを取り出して渡す ▼▼▼
            val result = rideRepository.sendRideRequest(rideOffer.id, passengerId, rideOffer.driverId)

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

