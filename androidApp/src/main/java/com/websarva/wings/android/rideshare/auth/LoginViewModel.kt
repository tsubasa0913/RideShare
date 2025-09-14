package com.websarva.wings.android.rideshare.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.auth.AuthRepository
import com.websarva.wings.android.rideshare.shared.data.auth.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UIの状態を表すデータクラス (変更なし)
data class LoginUiState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)

            val request = LoginRequest(email = email, pass = pass)
            val result = authRepository.login(request)

            // ▼▼▼ ここの result の中身が変わったため、処理を修正 ▼▼▼
            result.onSuccess { uid ->
                // 成功した場合、Firebaseから受け取ったuidがここに来る
                println("Firebase Login Success! UID: $uid")
                _uiState.value = LoginUiState(loginSuccess = true)
                // TODO: 取得したuidを端末に保存するなどの処理を後で追加
            }.onFailure { exception ->
                // 失敗した場合
                _uiState.value = LoginUiState(errorMessage = "ログインに失敗しました: ${exception.message}")
            }
        }
    }
}

