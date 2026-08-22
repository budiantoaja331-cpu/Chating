package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AppViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel() as T
            modelClass.isAssignableFrom(ChatListViewModel::class.java) -> ChatListViewModel() as T
            modelClass.isAssignableFrom(NearbyViewModel::class.java) -> NearbyViewModel() as T
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> StoryViewModel() as T
            modelClass.isAssignableFrom(UserProfileViewModel::class.java) -> UserProfileViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

class ChatRoomViewModelFactory(private val channelId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
            return ChatRoomViewModel(channelId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
