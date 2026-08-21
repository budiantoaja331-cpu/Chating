package com.example

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ChatListUiState {
    object Loading : ChatListUiState()
    data class Success(val channels: List<ChatChannel>) : ChatListUiState()
    data class Error(val message: String) : ChatListUiState()
}

class ChatListViewModel(val currentUserId: String = "my_user_id") : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")

    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        listenForChats()
    }

    private fun listenForChats() {
        chatsCollection
            .whereArrayContains("participants", currentUserId)
            // .orderBy removed to prevent Firestore composite index requirements
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ChatListViewModel", "Listen failed.", e)
                    _uiState.value = ChatListUiState.Error("Gagal memuat riwayat obrolan.")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val channels = mutableListOf<ChatChannel>()
                    for (doc in snapshot.documents) {
                        val channel = doc.toObject(ChatChannel::class.java)
                        if (channel != null) {
                            channels.add(channel)
                        }
                    }
                    channels.sortByDescending { it.lastMessageTime }
                    _uiState.value = ChatListUiState.Success(channels)
                }
            }
    }
}
