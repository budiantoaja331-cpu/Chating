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

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(friendId).get().await()
                if (doc.exists()) {
                    val profile = doc.toObject(UserProfile::class.java)
                    if (profile != null) {
                        _uiState.value = FriendProfileUiState.Success(profile)
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
