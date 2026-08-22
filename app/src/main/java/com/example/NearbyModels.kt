package com.example

data class NearbyUser(
    val id: String = "",
    val name: String = "",
    val bio: String = "",
    val distanceKm: Double = 0.0,
    val avatarUrl: String = "",
    val isOnline: Boolean = true
)

sealed interface NearbyUiState {
    object Loading : NearbyUiState
    data class Success(val users: List<NearbyUser>) : NearbyUiState
    data class Error(val message: String) : NearbyUiState
}
