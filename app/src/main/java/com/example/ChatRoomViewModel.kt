package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatRoomViewModel(val channelId: String) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatRoomUiState>(ChatRoomUiState.Loading)
    val uiState: StateFlow<ChatRoomUiState> = _uiState.asStateFlow()

    private val messages = mutableListOf<ChatMessage>()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = ChatRoomUiState.Loading
            messages.clear()
            messages.addAll(
                listOf(
                    ChatMessage("m1", "other", "Halo! Selamat datang di ChatMicAll.", System.currentTimeMillis() - 3600000),
                    ChatMessage("m2", "user_me", "Halo! Aplikasi ini keren sekali.", System.currentTimeMillis() - 1800000),
                    ChatMessage("m3", "other", "Semua fitur obrolan dan story bekerja dengan lancar!", System.currentTimeMillis() - 600000)
                )
            )
            _uiState.value = ChatRoomUiState.Success(messages.toList(), "Ruang Obrolan")
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderId = "user_me",
            text = text,
            timestamp = System.currentTimeMillis()
        )
        messages.add(newMsg)
        _uiState.value = ChatRoomUiState.Success(messages.toList(), "Ruang Obrolan")
    }
}
