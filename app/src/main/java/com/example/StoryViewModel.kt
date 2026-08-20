package com.example

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
    val likedByUsers: List<String> = emptyList()
)

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

    init {
        listenForStories()
    }

    private fun listenForStories() {
        storiesCollection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("StoryViewModel", "Listen failed.", e)
                    _uiState.value = StoryUiState.Error("Gagal memuat cerita: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val storiesList = mutableListOf<Story>()
                    for (doc in snapshot.documents) {
                        val story = doc.toObject(Story::class.java)
                        if (story != null) {
                            storiesList.add(story)
                        }
                    }
                    _uiState.value = StoryUiState.Success(storiesList)
                } else {
                    _uiState.value = StoryUiState.Success(emptyList())
                }
            }
    }

    fun addStory(content: String) {
        if (content.isBlank()) return
        
        viewModelScope.launch {
            try {
                val newStoryRef = storiesCollection.document()
                val story = Story(
                    id = newStoryRef.id,
                    authorId = currentUserId,
                    authorName = currentUserName,
                    authorHandle = "@user",
                    timestamp = System.currentTimeMillis(),
                    content = content
                )
                newStoryRef.set(story).await()
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error adding story", e)
            }
        }
    }

    fun toggleLike(storyId: String, currentLikes: List<String>) {
        viewModelScope.launch {
            try {
                val docRef = storiesCollection.document(storyId)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(docRef)
                    val story = snapshot.toObject(Story::class.java)
                    if (story != null) {
                        val newLikes = story.likedByUsers.toMutableList()
                        if (newLikes.contains(currentUserId)) {
                            newLikes.remove(currentUserId)
                        } else {
                            newLikes.add(currentUserId)
                        }
                        transaction.update(docRef, "likedByUsers", newLikes)
                        transaction.update(docRef, "likesCount", newLikes.size)
                    }
                }.await()
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error toggling like", e)
            }
        }
    }
}
