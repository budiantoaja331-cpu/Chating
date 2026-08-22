package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            val dummyUsers = listOf(
                NearbyUser("u1", "Dina Prasetya", "Menyukai fotografi & traveling", 0.5, "https://picsum.photos/104", true),
                NearbyUser("u2", "Rian Hidayat", "Pengembang aplikasi Android", 1.2, "https://picsum.photos/105", true),
                NearbyUser("u3", "Maya Putri", "Desainer UI/UX di Jakarta", 2.4, "https://picsum.photos/106", false)
            )
            _uiState.value = NearbyUiState.Success(dummyUsers)
        }
    }
}
