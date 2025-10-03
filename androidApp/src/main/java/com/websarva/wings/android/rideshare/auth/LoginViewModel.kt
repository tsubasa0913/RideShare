package com.websarva.wings.android.rideshare.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.auth.AuthRepository
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import com.websarva.wings.android.rideshare.shared.data.user.UserRepository
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
    private val userRepository = UserRepository() // ◀◀ 1. UserRepositoryを追加
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signInWithGoogle(idToken: String?) {
        if (idToken == null) {
            _uiState.value = LoginUiState(errorMessage = "Google Sign-In failed: No token provided.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            // accessTokenにはnullを渡します
            val result = authRepository.signInWithGoogle(idToken, null)

            result.onSuccess { uid ->
                println("Firebase Google Login Success! UID: $uid")
                UserSession.login(uid)
                // ▼▼▼ 2. ログイン成功後、プロフィールの存在確認と作成を行う ▼▼▼
                userRepository.checkAndCreateUserProfile().onSuccess {
                    _uiState.value = LoginUiState(loginSuccess = true)
                }.onFailure {
                    // プロフィール作成に失敗してもログイン自体は成功として扱う
                    _uiState.value = LoginUiState(loginSuccess = true)
                }
            }.onFailure { exception ->
                _uiState.value = LoginUiState(errorMessage = "Googleログインに失敗しました: ${exception.message}")
            }
        }
    }
}

