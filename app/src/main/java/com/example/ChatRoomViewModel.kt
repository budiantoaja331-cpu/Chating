package com.example

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class ChatRoomUiState {
    object Loading : ChatRoomUiState()
    data class Success(val messages: List<ChatMessage>) : ChatRoomUiState()
    data class Error(val message: String) : ChatRoomUiState()
}

class ChatRoomViewModel(
    private val otherUserId: String,
    private val otherUserName: String,
    private val currentUserId: String = "my_user_id",
    private val currentUserName: String = "My User"
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")
    private val channelId = getChannelId(currentUserId, otherUserId)

    private val _uiState = MutableStateFlow<ChatRoomUiState>(ChatRoomUiState.Loading)
    val uiState: StateFlow<ChatRoomUiState> = _uiState.asStateFlow()

    init {
        createChannelIfNotExists()
        listenForMessages()
    }

    private fun getChannelId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_${user2}" else "${user2}_${user1}"
    }

    private fun createChannelIfNotExists() {
        viewModelScope.launch {
            try {
                val channelDoc = chatsCollection.document(channelId).get().await()
                if (!channelDoc.exists()) {
                    val newChannel = ChatChannel(
                        id = channelId,
                        participants = listOf(currentUserId, otherUserId),
                        participantNames = mapOf(
                            currentUserId to currentUserName,
                            otherUserId to otherUserName
                        ),
                        lastMessage = "",
                        lastMessageTime = System.currentTimeMillis()
                    )
                    chatsCollection.document(channelId).set(newChannel).await()
                }
            } catch (e: Exception) {
                Log.e("ChatRoomViewModel", "Error creating channel", e)
            }
        }
    }

    private fun listenForMessages() {
        chatsCollection.document(channelId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ChatRoomViewModel", "Listen failed.", e)
                    _uiState.value = ChatRoomUiState.Error("Gagal memuat pesan.")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = mutableListOf<ChatMessage>()
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(ChatMessage::class.java)
                        if (message != null) {
                            messages.add(message)
                        }
                    }
                    _uiState.value = ChatRoomUiState.Success(messages)
                }
            }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            try {
                val messageRef = chatsCollection.document(channelId).collection("messages").document()
                val message = ChatMessage(
                    id = messageRef.id,
                    senderId = currentUserId,
                    text = text.trim(),
                    timestamp = System.currentTimeMillis()
                )
                
                // Save message
                messageRef.set(message).await()
                
                // Update channel last message using Set with merge to avoid not-found errors
                chatsCollection.document(channelId).set(
                    mapOf(
                        "lastMessage" to message.text,
                        "lastMessageTime" to message.timestamp
                    ), SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                Log.e("ChatRoomViewModel", "Error sending message", e)
            }
        }
    }
}

class ChatRoomViewModelFactory(private val otherUserId: String, private val otherUserName: String, private val currentUserId: String, private val currentUserName: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatRoomViewModel(otherUserId, otherUserName, currentUserId, currentUserName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
