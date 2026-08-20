package com.example

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class CallHistoryUiState {
    object Loading : CallHistoryUiState()
    data class Success(val calls: List<CallRecord>) : CallHistoryUiState()
    data class Error(val message: String) : CallHistoryUiState()
}

class CallHistoryViewModel(val currentUserId: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val callsCollection = db.collection("call_history")

    private val _uiState = MutableStateFlow<CallHistoryUiState>(CallHistoryUiState.Loading)
    val uiState: StateFlow<CallHistoryUiState> = _uiState.asStateFlow()

    init {
        fetchCallHistory()
    }

    private fun fetchCallHistory() {
        callsCollection
            .whereArrayContains("participants", currentUserId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CallHistoryVM", "Error fetching calls", error)
                    _uiState.value = CallHistoryUiState.Error(error.message ?: "Unknown error")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val calls = snapshot.documents.mapNotNull { it.toObject(CallRecord::class.java) }
                    _uiState.value = CallHistoryUiState.Success(calls)
                }
            }
    }
}
