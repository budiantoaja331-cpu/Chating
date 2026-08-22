package com.example

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val phoneNumber: String = ""
)

sealed interface UserProfileUiState {
    object Loading : UserProfileUiState
    data class Success(val profile: UserProfile) : UserProfileUiState
    data class Error(val message: String) : UserProfileUiState
}
