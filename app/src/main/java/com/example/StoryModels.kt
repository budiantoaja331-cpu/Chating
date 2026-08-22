package com.example

data class Story(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String = "",
    val mediaUrl: String = "",
    val caption: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val likedByCurrentUser: Boolean = false
)

sealed interface StoryUiState {
    object Loading : StoryUiState
    data class Success(val stories: List<Story>) : StoryUiState
    data class Error(val message: String) : StoryUiState
}
