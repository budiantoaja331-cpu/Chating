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

class ChatListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        loadChannels()
    }

    fun loadChannels() {
        viewModelScope.launch {
            _uiState.value = ChatListUiState.Loading
            try {
                val db = Firebase.firestore
                listenerRegistration?.remove()
                listenerRegistration = db.collection("channels")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null || snapshot == null) {
                            loadDummyChannels()
                            return@addSnapshotListener
                        }
                        val channels = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(ChatChannel::class.java)?.copy(id = doc.id)
                        }
                        if (channels.isEmpty()) {
                            loadDummyChannels()
                        } else {
                            _uiState.value = ChatListUiState.Success(channels)
                        }
                    }
            } catch (e: Exception) {
                loadDummyChannels()
            }
        }
    }

    private fun loadDummyChannels() {
        val dummyChannels = listOf(
            ChatChannel(
                id = "channel_1",
                name = "Grup Komunitas Video Call",
                lastMessage = "Halo semuanya! Ada yang mau obrolan video?",
                timestamp = System.currentTimeMillis() - 3600000,
                avatarUrl = "https://picsum.photos/101"
            ),
            ChatChannel(
                id = "channel_2",
                name = "Andi Wijaya",
                lastMessage = "Siap, nanti malam kita sambung lagi ya.",
                timestamp = System.currentTimeMillis() - 7200000,
                avatarUrl = "https://picsum.photos/102"
            ),
            ChatChannel(
                id = "channel_3",
                name = "Siti Rahma",
                lastMessage = "Terima kasih informasinya!",
                timestamp = System.currentTimeMillis() - 86400000,
                avatarUrl = "https://picsum.photos/103"
            )
        )
        _uiState.value = ChatListUiState.Success(dummyChannels)
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
