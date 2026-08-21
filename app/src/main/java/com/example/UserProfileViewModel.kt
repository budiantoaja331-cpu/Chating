package com.example

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@androidx.annotation.Keep data class UserProfile(
    val id: String = "",
    val name: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val nickname: String = "",
    val age: Int = 0,
    val interests: String = "",
    val isProfileComplete: Boolean = false,
    val blockedUsers: List<String> = emptyList(),
    val following: List<String> = emptyList(),
    val followers: List<String> = emptyList()
)

sealed class UserProfileUiState {
    object Loading : UserProfileUiState()
    data class Success(val profile: UserProfile) : UserProfileUiState()
    data class Error(val message: String) : UserProfileUiState()
}

class UserProfileViewModel(
    private val userId: String = "my_user_id",
    private val userName: String = "New User",
    private val profileImageUrl: String? = null
) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val profilesCollection = db.collection("users")
    
    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    
    private val _blockedUserProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val blockedUserProfiles: StateFlow<List<UserProfile>> = _blockedUserProfiles.asStateFlow()

    fun loadBlockedUsers(blockedUserIds: List<String>) {
        if (blockedUserIds.isEmpty()) {
            _blockedUserProfiles.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                // Fetch in chunks of 10 to respect Firestore whereIn limits
                val chunks = blockedUserIds.chunked(10)
                val profiles = mutableListOf<UserProfile>()
                for (chunk in chunks) {
                    val snapshot = profilesCollection.whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunk).get().await()
                    profiles.addAll(snapshot.toObjects(UserProfile::class.java))
                }
                _blockedUserProfiles.value = profiles
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error loading blocked users", e)
            }
        }
    }
    
    fun unblockUser(targetUserId: String) {
        viewModelScope.launch {
            try {
                // Remove from UserSessionManager immediately so other screens update
                UserSessionManager.unblockUser(targetUserId)
                
                // Then fetch the updated list 
                // The UserSessionManager is already updating Firestore
                
                // Update local blocked user profiles list
                _blockedUserProfiles.update { it.filter { profile -> profile.id != targetUserId } }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error unblocking user", e)
            }
        }
    }
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        _uiState.value = UserProfileUiState.Loading
        viewModelScope.launch {
            try {
                val document = profilesCollection.document(userId).get().await()
                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    if (profile != null) {
                        _uiState.value = UserProfileUiState.Success(profile)
                    } else {
                        _uiState.value = UserProfileUiState.Error("Failed to parse profile")
                    }
                } else {
                    // Profile doesn't exist yet, create a default one
                    val newProfile = UserProfile(id = userId, name = userName, bio = "Ini adalah bio saya.", avatarUrl = profileImageUrl ?: "")
                    _uiState.value = UserProfileUiState.Success(newProfile)
                }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error loading profile", e)
                _uiState.value = UserProfileUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun updateAvatar(uri: android.net.Uri, onComplete: (Boolean) -> Unit) {
        val currentState = _uiState.value
        if (currentState !is UserProfileUiState.Success) return

        viewModelScope.launch {
            try {
                val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
                val avatarRef = storageRef.child("avatars/$userId.jpg")
                avatarRef.putFile(uri).await()
                val downloadUrl = avatarRef.downloadUrl.await().toString()
                
                val updatedProfile = currentState.profile.copy(avatarUrl = downloadUrl)
                _uiState.value = UserProfileUiState.Success(updatedProfile)
                profilesCollection.document(userId).set(updatedProfile).await()
                onComplete(true)
            } catch (e: Exception) {
                android.util.Log.e("UserProfileViewModel", "Error updating avatar", e)
                onComplete(false)
            }
        }
    }

    fun updateProfile(name: String, bio: String, nickname: String = "", age: Int = 0, interests: String = "") {
        val currentState = _uiState.value
        if (currentState is UserProfileUiState.Success) {
            val updatedProfile = currentState.profile.copy(
                name = name, 
                bio = bio,
                nickname = if (nickname.isNotEmpty()) nickname else currentState.profile.nickname,
                age = if (age > 0) age else currentState.profile.age,
                interests = if (interests.isNotEmpty()) interests else currentState.profile.interests,
                isProfileComplete = true
            )
            _uiState.value = UserProfileUiState.Success(updatedProfile) // Optimistic update
            
            viewModelScope.launch {
                try {
                    profilesCollection.document(userId).set(updatedProfile).await()
                } catch (e: Exception) {
                    Log.e("UserProfileViewModel", "Error updating profile", e)
                    _uiState.value = UserProfileUiState.Error("Failed to update profile: ${e.message}")
                }
            }
        }
    }
}
