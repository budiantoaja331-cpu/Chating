package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            UserManager.currentUser.collect { profile ->
                if (profile != null) {
                    _uiState.value = UserProfileUiState.Success(profile)
                }
            }
        }
    }

    fun updateProfile(name: String, bio: String, phone: String) {
        val current = (uiState.value as? UserProfileUiState.Success)?.profile ?: return
        val updated = current.copy(name = name, bio = bio, phoneNumber = phone)
        UserManager.updateProfile(updated)
        _uiState.value = UserProfileUiState.Success(updated)
    }
}
