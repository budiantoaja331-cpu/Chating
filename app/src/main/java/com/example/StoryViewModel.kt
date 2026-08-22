package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<StoryUiState>(StoryUiState.Loading)
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null
    private val dummyStories = mutableListOf<Story>()

    init {
        loadStories()
    }

    fun loadStories() {
        viewModelScope.launch {
            _uiState.value = StoryUiState.Loading
            try {
                val db = Firebase.firestore
                listenerRegistration?.remove()
                listenerRegistration = db.collection("stories")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null || snapshot == null) {
                            loadDummyStories()
                            return@addSnapshotListener
                        }
                        val stories = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Story::class.java)?.copy(id = doc.id)
                        }
                        if (stories.isEmpty() && dummyStories.isEmpty()) {
                            loadDummyStories()
                        } else {
                            val combined = if (stories.isNotEmpty()) stories else dummyStories
                            _uiState.value = StoryUiState.Success(combined)
                        }
                    }
            } catch (e: Exception) {
                loadDummyStories()
            }
        }
    }

    private fun loadDummyStories() {
        if (dummyStories.isEmpty()) {
            dummyStories.addAll(
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
        }
        _uiState.value = StoryUiState.Success(dummyStories.toList())
    }

    fun toggleLike(storyId: String) {
        val currentUiState = _uiState.value
        if (currentUiState is StoryUiState.Success) {
            val list = currentUiState.stories.toMutableList()
            val index = list.indexOfFirst { it.id == storyId }
            if (index != -1) {
                val story = list[index]
                val newLiked = !story.likedByCurrentUser
                val newCount = if (story.likedByCurrentUser) story.likesCount - 1 else story.likesCount + 1
                val updated = story.copy(
                    likedByCurrentUser = newLiked,
                    likesCount = newCount
                )
                list[index] = updated
                _uiState.value = StoryUiState.Success(list)

                try {
                    val db = Firebase.firestore
                    db.collection("stories").document(storyId).update(
                        mapOf(
                            "likesCount" to newCount
                        )
                    )
                } catch (e: Exception) {
                    // Fallback
                }
            }
        }
    }

    fun addStory(caption: String, mediaUrl: String) {
        val currentUser = UserManager.currentUser.value
        val timestamp = System.currentTimeMillis()
        val newStory = Story(
            id = "story_$timestamp",
            userId = currentUser?.id ?: "user_me",
            userName = currentUser?.name ?: "Budianto",
            userAvatarUrl = currentUser?.avatarUrl ?: "https://picsum.photos/200",
            mediaUrl = mediaUrl.ifBlank { "https://picsum.photos/600/402" },
            caption = caption,
            timestamp = timestamp
        )

        try {
            val db = Firebase.firestore
            val storyMap = mapOf(
                "userId" to newStory.userId,
                "userName" to newStory.userName,
                "userAvatarUrl" to newStory.userAvatarUrl,
                "mediaUrl" to newStory.mediaUrl,
                "caption" to newStory.caption,
                "timestamp" to newStory.timestamp,
                "likesCount" to 0
            )
            db.collection("stories").add(storyMap)
        } catch (e: Exception) {
            dummyStories.add(0, newStory)
            _uiState.value = StoryUiState.Success(dummyStories.toList())
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
