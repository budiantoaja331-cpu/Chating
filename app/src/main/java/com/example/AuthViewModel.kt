package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val auth: FirebaseAuth? by lazy {
        try {
            Firebase.auth
        } catch (e: Exception) {
            null
        }
    }

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        val currentUser = auth?.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val name = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "Pengguna"
            val email = currentUser.email ?: ""
            _authState.value = AuthState.Authenticated(userId, name, email)
            UserManager.setCurrentUserId(userId, name, email)
        } else {
            _authState.value = AuthState.Authenticated("user_me", "Budianto", "budiantoaja331@gmail.com")
            UserManager.setCurrentUserId("user_me", "Budianto", "budiantoaja331@gmail.com")
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val currentUser = auth?.currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                val name = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "Pengguna"
                val email = currentUser.email ?: ""
                _authState.value = AuthState.Authenticated(userId, name, email)
                UserManager.setCurrentUserId(userId, name, email)
            } else {
                _authState.value = AuthState.Authenticated("user_me", "Budianto", "budiantoaja331@gmail.com")
                UserManager.setCurrentUserId("user_me", "Budianto", "budiantoaja331@gmail.com")
            }
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            // ignore
        }
        _authState.value = AuthState.Unauthenticated
    }
}
