package com.websarva.wings.android.rideshare.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.websarva.wings.android.rideshare.shared.data.model.User
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import com.websarva.wings.android.rideshare.shared.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val saveSuccessMessage: String? = null,
    val errorMessage: String? = null
)

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = UserSession.getCurrentUserId()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "ログインしていません。") }
                return@launch
            }

            val result = userRepository.getUserProfile(userId)
            result.onSuccess { userProfile ->
                _uiState.update { it.copy(isLoading = false, user = userProfile) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "プロフィールの読み込みに失敗: ${e.message}") }
            }
        }
    }

    fun saveUserProfile(name: String, grade: String, department: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user ?: return@launch
            val updatedUser = currentUser.copy(
                name = name,
                grade = grade,
                department = department
            )
            val result = userRepository.saveUserProfile(updatedUser)
            result.onSuccess {
                _uiState.update { it.copy(user = updatedUser, saveSuccessMessage = "プロフィールを保存しました。") }
            }.onFailure {
                _uiState.update { it.copy(errorMessage = "プロフィールの保存に失敗しました。") }
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(saveSuccessMessage = null) }
    }
}
