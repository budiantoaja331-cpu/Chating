package com.example

sealed interface AuthState {
    object Unauthenticated : AuthState
    object Loading : AuthState
    data class Authenticated(val userId: String, val userName: String, val email: String) : AuthState
    data class Error(val message: String) : AuthState
}
