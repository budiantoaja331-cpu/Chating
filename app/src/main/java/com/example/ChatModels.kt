package com.example

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatChannel(
    val id: String = "",
    val name: String = "",
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val avatarUrl: String = "",
    val participantIds: List<String> = emptyList()
)

sealed interface ChatListUiState {
    object Loading : ChatListUiState
    data class Success(val channels: List<ChatChannel>) : ChatListUiState
    data class Error(val message: String) : ChatListUiState
}

sealed interface ChatRoomUiState {
    object Loading : ChatRoomUiState
    data class Success(
        val messages: List<ChatMessage>,
        val channelName: String
    ) : ChatRoomUiState
    data class Error(val message: String) : ChatRoomUiState
}
