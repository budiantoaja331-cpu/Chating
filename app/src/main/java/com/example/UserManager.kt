package com.example

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserManager {
    private val _currentUser = MutableStateFlow<UserProfile?>(
        UserProfile(
            id = "user_me",
            name = "Budianto",
            email = "budiantoaja331@gmail.com",
            bio = "Halo! Saya menggunakan ChatMicAll.",
            avatarUrl = "https://picsum.photos/200"
        )
    )
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    fun setCurrentUserId(id: String, name: String, email: String) {
        val updated = (_currentUser.value ?: UserProfile()).copy(
            id = id,
            name = name,
            email = email
        )
        _currentUser.value = updated
        syncWithFirestore(updated)
    }

    fun updateProfile(newProfile: UserProfile) {
        _currentUser.value = newProfile
        syncWithFirestore(newProfile)
    }

    private fun syncWithFirestore(profile: UserProfile) {
        try {
            val db = Firebase.firestore
            val userMap = mapOf(
                "id" to profile.id,
                "name" to profile.name,
                "email" to profile.email,
                "bio" to profile.bio,
                "avatarUrl" to profile.avatarUrl,
                "phoneNumber" to profile.phoneNumber,
                "isOnline" to true
            )
            db.collection("users").document(profile.id).set(userMap)
        } catch (e: Exception) {
            // Fallback for offline or uninitialized firebase
        }
    }
}
