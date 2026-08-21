package com.example

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class NotificationUiState {
    object Loading : NotificationUiState()
    data class Success(val notifications: List<Notification>) : NotificationUiState()
    data class Error(val message: String) : NotificationUiState()
}

class NotificationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()
    
    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        if (currentUserId == null) {
            _uiState.value = NotificationUiState.Error("User not logged in")
            return
        }
        
        _uiState.value = NotificationUiState.Loading
        listenerRegistration?.remove()
        
        listenerRegistration = db.collection("notifications")
            .whereEqualTo("targetUserId", currentUserId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("NotificationVM", "Listen failed.", e)
                    _uiState.value = NotificationUiState.Error(e.message ?: "Error loading notifications")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notifications = snapshot.toObjects(Notification::class.java)
                    _uiState.value = NotificationUiState.Success(notifications)
                }
            }
    }
    
    fun markAsRead(notificationId: String) {
        db.collection("notifications").document(notificationId).update("read", true)
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
