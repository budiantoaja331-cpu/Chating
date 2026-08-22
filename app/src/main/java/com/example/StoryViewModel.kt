package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<StoryUiState>(StoryUiState.Loading)
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    private val stories = mutableListOf<Story>()

    init {
        loadStories()
    }

    fun loadStories() {
        viewModelScope.launch {
            _uiState.value = StoryUiState.Loading
            stories.clear()
            stories.addAll(
                listOf(
                    Story(
                        id = "s1",
                        userId = "u1",
                        userName = "Dina Prasetya",
                        userAvatarUrl = "https://picsum.photos/104",
                        mediaUrl = "https://picsum.photos/600/400",
                        caption = "Pemandangan sore yang menakjubkan! 🌅",
                        timestamp = System.currentTimeMillis() - 7200000,
                        likesCount = 12,
                        likedByCurrentUser = false
                    ),
                    Story(
                        id = "s2",
                        userId = "u2",
                        userName = "Rian Hidayat",
                        userAvatarUrl = "https://picsum.photos/105",
                        mediaUrl = "https://picsum.photos/600/401",
                        caption = "Kopi pagi untuk semangat coding hari ini ☕💻",
                        timestamp = System.currentTimeMillis() - 14400000,
                        likesCount = 25,
                        likedByCurrentUser = true
                    )
                )
            )
            _uiState.value = StoryUiState.Success(stories.toList())
        }
    }

    fun toggleLike(storyId: String) {
        val index = stories.indexOfFirst { it.id == storyId }
        if (index != -1) {
            val story = stories[index]
            val updated = story.copy(
                likedByCurrentUser = !story.likedByCurrentUser,
                likesCount = if (story.likedByCurrentUser) story.likesCount - 1 else story.likesCount + 1
            )
            stories[index] = updated
            _uiState.value = StoryUiState.Success(stories.toList())
        }
    }

    fun addStory(caption: String, mediaUrl: String) {
        val newStory = Story(
            id = "story_${System.currentTimeMillis()}",
            userId = "user_me",
            userName = "Budianto",
            userAvatarUrl = "https://picsum.photos/200",
            mediaUrl = mediaUrl.ifBlank { "https://picsum.photos/600/402" },
            caption = caption,
            timestamp = System.currentTimeMillis()
        )
        stories.add(0, newStory)
        _uiState.value = StoryUiState.Success(stories.toList())
    }
}
