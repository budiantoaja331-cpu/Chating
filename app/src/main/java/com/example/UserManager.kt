package com.example

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

    fun updateProfile(newProfile: UserProfile) {
        _currentUser.value = newProfile
    }
}
