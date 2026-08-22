package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(
        AuthState.Authenticated("user_me", "Budianto", "budiantoaja331@gmail.com")
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signInWithGoogle() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _authState.value = AuthState.Authenticated("user_me", "Budianto", "budiantoaja331@gmail.com")
        }
    }

    fun signOut() {
        _authState.value = AuthState.Unauthenticated
    }
}
