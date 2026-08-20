package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AppViewModelFactory(
    private val profileImageUrl: String? = null,
    private val userId: String,
    private val userName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NearbyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NearbyViewModel(userId, userName) as T
        }
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(userId, userName) as T
        }
        if (modelClass.isAssignableFrom(ChatListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatListViewModel(userId) as T
        }
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserProfileViewModel(userId, userName, profileImageUrl) as T
        }
        if (modelClass.isAssignableFrom(CallHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CallHistoryViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
