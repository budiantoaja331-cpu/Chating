package com.example
import androidx.annotation.Keep

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Keep
data class ActiveCall(
    val id: String = "",
    val callerId: String = "",
    val callerName: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val channelId: String = "",
    val isVideoCall: Boolean = false,
    val status: String = "ringing", // ringing, accepted, rejected, ended
    val timestamp: Long = 0L
)

class ActiveCallManager(private val currentUserId: String) {
    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null
    
    private val _incomingCall = MutableStateFlow<ActiveCall?>(null)
    val incomingCall: StateFlow<ActiveCall?> = _incomingCall.asStateFlow()

    fun startListening() {
        if (currentUserId.isEmpty()) return
        
        listener = db.collection("active_calls")
            .whereEqualTo("receiverId", currentUserId)
            .whereEqualTo("status", "ringing")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ActiveCallManager", "Listen failed", error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null && !snapshot.isEmpty) {
                    val call = snapshot.documents.first().toObject(ActiveCall::class.java)
                    _incomingCall.value = call
                } else {
                    _incomingCall.value = null
                }
            }
    }

    fun stopListening() {
        listener?.remove()
        listener = null
    }

    fun acceptCall(callId: String) {
        db.collection("active_calls").document(callId)
            .update("status", "accepted")
        
        // Update call history status to completed
        db.collection("call_history").document(callId)
            .update("status", "completed")
            
        _incomingCall.value = null
    }

    fun rejectCall(callId: String) {
        db.collection("active_calls").document(callId)
            .update("status", "rejected")
            
        // Update call history status to rejected
        db.collection("call_history").document(callId)
            .update("status", "rejected")
            
        _incomingCall.value = null
    }
    
    fun initiateCall(callerName: String, receiverId: String, receiverName: String, isVideo: Boolean, channelId: String) {
        val callId = db.collection("active_calls").document().id
        val call = ActiveCall(
            id = callId,
            callerId = currentUserId,
            callerName = callerName,
            receiverId = receiverId,
            receiverName = receiverName,
            channelId = channelId,
            isVideoCall = isVideo,
            status = "ringing",
            timestamp = System.currentTimeMillis()
        )
        db.collection("active_calls").document(callId).set(call)
        
        // Log to call history with missed status initially. 
        // It will be updated if accepted/rejected.
        val callRecord = CallRecord(
            id = callId,
            participants = listOf(currentUserId, receiverId),
            callerId = currentUserId,
            callerName = callerName,
            receiverId = receiverId,
            receiverName = receiverName,
            timestamp = call.timestamp,
            durationSeconds = 0,
            isVideoCall = isVideo,
            status = "missed",
            channelId = channelId
        )
        db.collection("call_history").document(callId).set(callRecord)
    }
}
