package com.example

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PresenceState(
    val state: String = "offline",
    val last_changed: Long = 0L
)

class PresenceManager {
    private val db = FirebaseDatabase.getInstance()
    private val connectedRef = db.getReference(".info/connected")
    private var currentUserRef: com.google.firebase.database.DatabaseReference? = null
    private var connectedListener: ValueEventListener? = null

    // Cache of other users' presence
    private val _presenceMap = MutableStateFlow<Map<String, PresenceState>>(emptyMap())
    val presenceMap: StateFlow<Map<String, PresenceState>> = _presenceMap.asStateFlow()

    fun startTracking(userId: String) {
        if (userId.isEmpty()) return
        
        currentUserRef = db.getReference("status/$userId")
        
        connectedListener = connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    val con = currentUserRef
                    if (con != null) {
                        con.onDisconnect().setValue(
                            mapOf(
                                "state" to "offline",
                                "last_changed" to com.google.firebase.database.ServerValue.TIMESTAMP
                            )
                        ).addOnCompleteListener {
                            // The onDisconnect is set, now update the current status to online
                            con.setValue(
                                mapOf(
                                    "state" to "online",
                                    "last_changed" to com.google.firebase.database.ServerValue.TIMESTAMP
                                )
                            )
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("PresenceManager", "Listener was cancelled")
            }
        })
        
        // Listen to all users' statuses
        db.getReference("status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newMap = mutableMapOf<String, PresenceState>()
                for (child in snapshot.children) {
                    val userId = child.key ?: continue
                    val state = child.child("state").getValue(String::class.java) ?: "offline"
                    val lastChanged = child.child("last_changed").getValue(Long::class.java) ?: 0L
                    newMap[userId] = PresenceState(state, lastChanged)
                }
                _presenceMap.value = newMap
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun stopTracking() {
        connectedListener?.let { connectedRef.removeEventListener(it) }
        currentUserRef?.setValue(
            mapOf(
                "state" to "offline",
                "last_changed" to com.google.firebase.database.ServerValue.TIMESTAMP
            )
        )
    }
}

// Singleton for easy access across ViewModels if needed
object PresenceManagerInstance {
    val instance = PresenceManager()
}
