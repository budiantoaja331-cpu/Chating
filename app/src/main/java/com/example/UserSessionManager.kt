package com.example

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserSessionManager {
    private val db = FirebaseFirestore.getInstance()
    private val _blockedUsers = MutableStateFlow<List<String>>(emptyList())
    val blockedUsers: StateFlow<List<String>> = _blockedUsers.asStateFlow()

    private var currentUserId: String? = null
    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    fun startListening(userId: String) {
        if (currentUserId == userId) return
        currentUserId = userId
        listenerRegistration?.remove()
        listenerRegistration = db.collection("users").document(userId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("UserSessionManager", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java)
                _blockedUsers.value = profile?.blockedUsers ?: emptyList()
            }
        }
    }

    fun blockUser(blockedUserId: String) {
        val uid = currentUserId ?: return
        val currentBlocked = _blockedUsers.value.toMutableList()
        if (!currentBlocked.contains(blockedUserId)) {
            currentBlocked.add(blockedUserId)
            db.collection("users").document(uid).update("blockedUsers", currentBlocked)
        }
    }
    
    fun unblockUser(blockedUserId: String) {
        val uid = currentUserId ?: return
        val currentBlocked = _blockedUsers.value.toMutableList()
        if (currentBlocked.contains(blockedUserId)) {
            currentBlocked.remove(blockedUserId)
            db.collection("users").document(uid).update("blockedUsers", currentBlocked)
        }
    }
}
