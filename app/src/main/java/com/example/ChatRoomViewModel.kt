package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatRoomViewModel(val channelId: String) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatRoomUiState>(ChatRoomUiState.Loading)
    val uiState: StateFlow<ChatRoomUiState> = _uiState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null
    private val dummyMessages = mutableListOf<ChatMessage>()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = ChatRoomUiState.Loading
            try {
                val db = Firebase.firestore
                listenerRegistration?.remove()
                listenerRegistration = db.collection("channels")
                    .document(channelId)
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null || snapshot == null) {
                            loadDummyMessages()
                            return@addSnapshotListener
                        }
                        val messages = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
                        }
                        if (messages.isEmpty() && dummyMessages.isEmpty()) {
                            loadDummyMessages()
                        } else {
                            val combined = if (messages.isNotEmpty()) messages else dummyMessages
                            _uiState.value = ChatRoomUiState.Success(combined, "Ruang Obrolan")
                        }
                    }
            } catch (e: Exception) {
                loadDummyMessages()
            }
        }
    }

    private fun loadDummyMessages() {
        if (dummyMessages.isEmpty()) {
            val currentUserId = UserManager.currentUser.value?.id ?: "user_me"
            dummyMessages.addAll(
                listOf(
                    ChatMessage("m1", "other", "Halo! Selamat datang di ChatMicAll.", System.currentTimeMillis() - 3600000),
                    ChatMessage("m2", currentUserId, "Halo! Aplikasi ini keren sekali.", System.currentTimeMillis() - 1800000),
                    ChatMessage("m3", "other", "Semua fitur obrolan dan story terhubung ke Firebase!", System.currentTimeMillis() - 600000)
                )
            )
        }
        _uiState.value = ChatRoomUiState.Success(dummyMessages.toList(), "Ruang Obrolan")
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val currentUserId = UserManager.currentUser.value?.id ?: "user_me"
        val timestamp = System.currentTimeMillis()
        val newMsg = ChatMessage(
            id = "msg_$timestamp",
            senderId = currentUserId,
            text = text,
            timestamp = timestamp
        )

        try {
            val db = Firebase.firestore
            val msgMap = mapOf(
                "senderId" to currentUserId,
                "text" to text,
                "timestamp" to timestamp
            )
            db.collection("channels")
                .document(channelId)
                .collection("messages")
                .add(msgMap)

            db.collection("channels")
                .document(channelId)
                .set(
                    mapOf(
                        "id" to channelId,
                        "name" to "Ruang Obrolan",
                        "lastMessage" to text,
                        "timestamp" to timestamp
                    )
                )
        } catch (e: Exception) {
            // Local fallback
            dummyMessages.add(newMsg)
            _uiState.value = ChatRoomUiState.Success(dummyMessages.toList(), "Ruang Obrolan")
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
