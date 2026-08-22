package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            UserManager.currentUser.collect { profile ->
                if (profile != null) {
                    _uiState.value = UserProfileUiState.Success(profile)
                    try {
                        val db = Firebase.firestore
                        db.collection("users").document(profile.id).get()
                            .addOnSuccessListener { doc ->
                                if (doc.exists()) {
                                    val firestoreProfile = doc.toObject(UserProfile::class.java)?.copy(id = doc.id)
                                    if (firestoreProfile != null) {
                                        UserManager.updateProfile(firestoreProfile)
                                    }
                                }
                            }
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }
        }
    }

    fun updateProfile(name: String, bio: String, phone: String) {
        val current = (uiState.value as? UserProfileUiState.Success)?.profile ?: return
        val updated = current.copy(name = name, bio = bio, phoneNumber = phone)
        UserManager.updateProfile(updated)
        _uiState.value = UserProfileUiState.Success(updated)
    }
}
