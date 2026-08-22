package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NearbyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<NearbyUiState>(NearbyUiState.Loading)
    val uiState: StateFlow<NearbyUiState> = _uiState.asStateFlow()

    init {
        fetchLocationAndNearbyUsers()
    }

    fun fetchLocationAndNearbyUsers() {
        viewModelScope.launch {
            _uiState.value = NearbyUiState.Loading
            try {
                val db = Firebase.firestore
                db.collection("users").get().addOnSuccessListener { snapshot ->
                    val users = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(NearbyUser::class.java)?.copy(id = doc.id)
                    }.filter { it.id != UserManager.currentUser.value?.id }

                    if (users.isEmpty()) {
                        loadDummyUsers()
                    } else {
                        _uiState.value = NearbyUiState.Success(users)
                    }
                }.addOnFailureListener {
                    loadDummyUsers()
                }
            } catch (e: Exception) {
                loadDummyUsers()
            }
        }
    }

    private fun loadDummyUsers() {
        val dummyUsers = listOf(
            NearbyUser("u1", "Dina Prasetya", "Menyukai fotografi & traveling", 0.5, "https://picsum.photos/104", true),
            NearbyUser("u2", "Rian Hidayat", "Pengembang aplikasi Android", 1.2, "https://picsum.photos/105", true),
            NearbyUser("u3", "Maya Putri", "Desainer UI/UX di Jakarta", 2.4, "https://picsum.photos/106", false)
        )
        _uiState.value = NearbyUiState.Success(dummyUsers)
    }
}
