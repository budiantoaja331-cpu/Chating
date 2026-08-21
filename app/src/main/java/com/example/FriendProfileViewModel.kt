package com.example

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class FriendProfileUiState {
    object Loading : FriendProfileUiState()
    data class Success(val profile: UserProfile) : FriendProfileUiState()
    data class Error(val message: String) : FriendProfileUiState()
}

class FriendProfileViewModel(
    private val friendId: String
) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow<FriendProfileUiState>(FriendProfileUiState.Loading)
    val uiState: StateFlow<FriendProfileUiState> = _uiState.asStateFlow()
    
    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing.asStateFlow()

    init {
        checkIfFollowing()
        loadProfile()
    }

    private fun checkIfFollowing() {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(currentUser).get().await()
                if (doc.exists()) {
                    val profile = doc.toObject(UserProfile::class.java)
                    _isFollowing.value = profile?.following?.contains(friendId) ?: false
                }
            } catch (e: Exception) {
                Log.e("FriendProfile", "Error checking following state", e)
            }
        }
    }
    
    fun toggleFollow() {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        val currentlyFollowing = _isFollowing.value
        _isFollowing.value = !currentlyFollowing
        
        viewModelScope.launch {
            try {
                if (currentlyFollowing) {
                    db.collection("users").document(currentUser).update("following", com.google.firebase.firestore.FieldValue.arrayRemove(friendId)).await()
                    db.collection("users").document(friendId).update("followers", com.google.firebase.firestore.FieldValue.arrayRemove(currentUser)).await()
                } else {
                    db.collection("users").document(currentUser).update("following", com.google.firebase.firestore.FieldValue.arrayUnion(friendId)).await()
                    db.collection("users").document(friendId).update("followers", com.google.firebase.firestore.FieldValue.arrayUnion(currentUser)).await()
                }
            } catch (e: Exception) {
                Log.e("FriendProfile", "Error toggling follow", e)
                _isFollowing.value = currentlyFollowing // revert on failure
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(friendId).get().await()
                if (doc.exists()) {
                    val profile = doc.toObject(UserProfile::class.java)
                    if (profile != null) {
                        _uiState.value = FriendProfileUiState.Success(profile)
                        
                        // Send visit notification
                        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        if (currentUser != null && currentUser.uid != friendId) {
                            val myDoc = db.collection("users").document(currentUser.uid).get().await()
                            val myProfile = myDoc.toObject(UserProfile::class.java)
                            if (myProfile != null) {
                                val notif = Notification(
                                    targetUserId = friendId,
                                    sourceUserId = currentUser.uid,
                                    sourceUserName = myProfile.name,
                                    sourceUserAvatar = myProfile.avatarUrl,
                                    type = "visit"
                                )
                                db.collection("notifications").document(notif.id).set(notif)
                            }
                        }
                    } else {
                        _uiState.value = FriendProfileUiState.Error("Data profil tidak valid.")
                    }
                } else {
                    _uiState.value = FriendProfileUiState.Error("Pengguna tidak ditemukan.")
                }
            } catch (e: Exception) {
                Log.e("FriendProfile", "Error loading profile", e)
                _uiState.value = FriendProfileUiState.Error("Gagal memuat profil.")
            }
        }
    }
}

class FriendProfileViewModelFactory(private val friendId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendProfileViewModel(friendId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
