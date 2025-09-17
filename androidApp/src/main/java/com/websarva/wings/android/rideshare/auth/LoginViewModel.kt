package com.websarva.wings.android.rideshare.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.auth.AuthRepository
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * LoginScreenから受け取ったIDトークンでサインイン処理を開始する
     */
    fun signInWithGoogle(idToken: String?) {
        // トークンがnullの場合はエラー
        if (idToken == null) {
            _uiState.value = LoginUiState(errorMessage = "Google Sign-In failed: No token provided.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            // ▼▼▼ authRepositoryの呼び出しを修正 ▼▼▼
            val result = authRepository.signInWithGoogle(idToken, null) // accessTokenにnullを渡す

            result.onSuccess { uid ->
                println("Firebase Google Login Success! UID: $uid")
                UserSession.login(uid)
                _uiState.value = LoginUiState(loginSuccess = true)
            }.onFailure { exception ->
                _uiState.value = LoginUiState(errorMessage = "Googleログインに失敗しました: ${exception.message}")
            }
        }
    }
}

