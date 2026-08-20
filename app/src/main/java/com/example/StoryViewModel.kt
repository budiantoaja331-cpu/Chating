package com.example

import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Keep
data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val authorId: String = "",
    val authorName: String = "",
    val authorHandle: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val content: String = ""
) {
    @get:com.google.firebase.firestore.Exclude val formattedTime: String
        get() {
            val diff = System.currentTimeMillis() - timestamp
            val minute = 60 * 1000L
            val hour = 60 * minute
            val day = 24 * hour
            return when {
                diff < minute -> "now"
                diff < hour -> "${diff / minute}m ago"
                diff < day -> "${diff / hour}h ago"
                else -> "${diff / day}d ago"
            }
        }
}

@Keep
data class Story(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorHandle: String = "",
    val timestamp: Long = 0L,
    val content: String = "",
    val hasImage: Boolean = false,
    val imageUrl: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val comments: List<Comment> = emptyList(),
    val likedByUsers: List<String> = emptyList(),
    val bookmarkedByUsers: List<String> = emptyList()
) {
    @get:com.google.firebase.firestore.Exclude val formattedTime: String
        get() {
            val diff = System.currentTimeMillis() - timestamp
            val minute = 60 * 1000L
            val hour = 60 * minute
            val day = 24 * hour
            return when {
                diff < minute -> "now"
                diff < hour -> "${diff / minute}m ago"
                diff < day -> "${diff / hour}h ago"
                else -> "${diff / day}d ago"
            }
        }
}

sealed class StoryUiState {
    object Loading : StoryUiState()
    data class Success(val stories: List<Story>) : StoryUiState()
    data class Error(val message: String) : StoryUiState()
}

class StoryViewModel(val currentUserId: String = "my_user_id", private val currentUserName: String = "Anonim") : ViewModel() {
    
    private val db = FirebaseFirestore.getInstance()
    private val storiesCollection = db.collection("stories")

    private val _uiState = MutableStateFlow<StoryUiState>(StoryUiState.Loading)
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadStories()
    }

    private fun loadStories() {
        _uiState.value = StoryUiState.Loading
        fetchStoriesFromFirestore()
    }

    private fun fetchStoriesFromFirestore() {
        viewModelScope.launch {
            try {
                val snapshot = storiesCollection.orderBy("timestamp", Query.Direction.DESCENDING).get().await()
                val stories = snapshot.toObjects(Story::class.java)
                
                if (stories.isEmpty()) {
                    seedDummyDataToFirestore()
                } else {
                    _uiState.value = StoryUiState.Success(stories)
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error fetching stories", e)
                _uiState.value = StoryUiState.Error(e.message ?: "Failed to fetch stories")
            }
        }
    }
    
    private suspend fun seedDummyDataToFirestore() {
        val dummyStories = listOf(
            Story(
                id = UUID.randomUUID().toString(),
                authorId = "user1",
                authorName = "Budi Santoso",
                authorHandle = "@budis",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                content = "Hari ini cuaca sangat cerah! Waktunya ngoding Android pakai Jetpack Compose. 🚀",
                likesCount = 12,
                commentsCount = 3
            ),
            Story(
                id = UUID.randomUUID().toString(),
                authorId = "user2",
                authorName = "Siti Aminah",
                authorHandle = "@sitia",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                content = "Baru saja selesai membaca buku tentang Clean Architecture. Menarik sekali bagaimana kita bisa memisahkan logic dari UI secara rapi! 📚💡",
                likesCount = 24,
                commentsCount = 8
            ),
            Story(
                id = UUID.randomUUID().toString(),
                authorId = "user3",
                authorName = "Ahmad Fikri",
                authorHandle = "@fikri_ahmad",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                content = "Ada yang mau mabar Mobile Legends malam ini? 🎮 Siap push rank sampai mythic!",
                likesCount = 45,
                commentsCount = 15
            ),
            Story(
                id = UUID.randomUUID().toString(),
                authorId = "user4",
                authorName = "Dewi Lestari",
                authorHandle = "@dewilest",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
                content = "Menikmati senja sambil minum kopi sore ini. Tidak ada yang lebih menenangkan daripada ini. ☕🌇",
                likesCount = 112,
                commentsCount = 20
            )
        )
        
        try {
            for (story in dummyStories) {
                storiesCollection.document(story.id).set(story).await()
            }
            _uiState.value = StoryUiState.Success(dummyStories.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            Log.e("StoryViewModel", "Error seeding data", e)
            _uiState.value = StoryUiState.Error("Failed to seed dummy data")
        }
    }

    fun addStory(content: String) {
        if (content.isBlank()) return
        
        val newStory = Story(
            id = UUID.randomUUID().toString(),
            authorId = currentUserId,
            authorName = currentUserName,
            authorHandle = "@user",
            timestamp = System.currentTimeMillis(),
            content = content,
            likesCount = 0,
            commentsCount = 0
        )
        
        // Optimistic update
        val currentState = _uiState.value
        if (currentState is StoryUiState.Success) {
            val updatedStories = listOf(newStory) + currentState.stories
            _uiState.value = StoryUiState.Success(updatedStories)
        }
        
        viewModelScope.launch {
            try {
                storiesCollection.document(newStory.id).set(newStory).await()
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error adding story", e)
                // In a real app, handle failure (e.g., revert optimistic update and show toast)
            }
        }
    }

    fun toggleBookmark(storyId: String) {
        val currentState = _uiState.value
        if (currentState is StoryUiState.Success) {
            val stories = currentState.stories.toMutableList()
            val index = stories.indexOfFirst { it.id == storyId }
            if (index != -1) {
                val story = stories[index]
                val newBookmarks = story.bookmarkedByUsers.toMutableList()
                if (newBookmarks.contains(currentUserId)) {
                    newBookmarks.remove(currentUserId)
                } else {
                    newBookmarks.add(currentUserId)
                }
                
                val updatedStory = story.copy(bookmarkedByUsers = newBookmarks)
                stories[index] = updatedStory
                _uiState.value = StoryUiState.Success(stories.toList())
                
                // Update Firestore
                viewModelScope.launch {
                    try {
                        storiesCollection.document(storyId).set(updatedStory).await()
                    } catch (e: Exception) {
                        Log.e("StoryViewModel", "Error toggling bookmark", e)
                    }
                }
            }
        }
    }

    fun toggleLike(storyId: String, currentLikes: List<String>) {
        val currentState = _uiState.value
        if (currentState is StoryUiState.Success) {
            val stories = currentState.stories.toMutableList()
            val index = stories.indexOfFirst { it.id == storyId }
            if (index != -1) {
                val story = stories[index]
                val newLikes = story.likedByUsers.toMutableList()
                if (newLikes.contains(currentUserId)) {
                    newLikes.remove(currentUserId)
                } else {
                    newLikes.add(currentUserId)
                }
                
                val updatedStory = story.copy(
                    likedByUsers = newLikes,
                    likesCount = newLikes.size
                )
                stories[index] = updatedStory
                _uiState.value = StoryUiState.Success(stories.toList())
                
                // Update Firestore
                viewModelScope.launch {
                    try {
                        storiesCollection.document(storyId).set(updatedStory).await()
                    } catch (e: Exception) {
                        Log.e("StoryViewModel", "Error toggling like", e)
                    }
                }
            }
        }
    }

    fun addComment(storyId: String, content: String) {
        val currentState = _uiState.value
        if (currentState is StoryUiState.Success) {
            val stories = currentState.stories.toMutableList()
            val index = stories.indexOfFirst { it.id == storyId }
            if (index != -1) {
                val story = stories[index]
                val newComment = Comment(
                    authorId = currentUserId,
                    authorName = currentUserName,
                    authorHandle = "@user",
                    content = content
                )
                val updatedComments = story.comments + newComment
                val updatedStory = story.copy(
                    comments = updatedComments,
                    commentsCount = updatedComments.size
                )
                stories[index] = updatedStory
                _uiState.value = StoryUiState.Success(stories.toList())
                
                // Update Firestore
                viewModelScope.launch {
                    try {
                        storiesCollection.document(storyId).set(updatedStory).await()
                    } catch (e: Exception) {
                        Log.e("StoryViewModel", "Error adding comment", e)
                    }
                }
            }
        }
    }

    fun refreshStories() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val snapshot = storiesCollection.orderBy("timestamp", Query.Direction.DESCENDING).get().await()
                val stories = snapshot.toObjects(Story::class.java)
                _uiState.value = StoryUiState.Success(stories)
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error refreshing stories", e)
                // Avoid overriding success state with error on refresh if there's old data,
                // but for simplicity we'll just show the error.
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
