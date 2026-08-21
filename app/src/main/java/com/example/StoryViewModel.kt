package com.example

import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
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
    val authorAvatarUrl: String = "",
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
    val authorAvatarUrl: String = "",
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
    
    private val _feedFilter = MutableStateFlow("All")
    val feedFilter: StateFlow<String> = _feedFilter.asStateFlow()
    
    private var currentUserFollowing: List<String> = emptyList()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchStoriesResult = MutableStateFlow<List<Story>>(emptyList())
    val searchStoriesResult: StateFlow<List<Story>> = _searchStoriesResult.asStateFlow()

    private val _searchUsersResult = MutableStateFlow<List<UserProfile>>(emptyList())
    val searchUsersResult: StateFlow<List<UserProfile>> = _searchUsersResult.asStateFlow()
    
    private var searchJob: kotlinx.coroutines.Job? = null

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchStoriesResult.value = emptyList()
            _searchUsersResult.value = emptyList()
            return
        }
        performSearch(query)
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            _isSearching.value = true
            try {
                val storiesSnapshot = storiesCollection
                    .whereGreaterThanOrEqualTo("content", query)
                    .whereLessThanOrEqualTo("content", query + "\uf8ff")
                    .limit(20)
                    .get().await()
                _searchStoriesResult.value = storiesSnapshot.toObjects(Story::class.java)

                val usersSnapshot = db.collection("users")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff")
                    .limit(20)
                    .get().await()
                _searchUsersResult.value = usersSnapshot.toObjects(UserProfile::class.java)
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error searching", e)
            } finally {
                _isSearching.value = false
            }
        }
    }
    
    private val _savedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val savedPostIds: StateFlow<Set<String>> = _savedPostIds.asStateFlow()

    private var currentBlockedUsers: List<String> = emptyList()
    private var rawStories: List<Story> = emptyList()
    private var userProfileListener: com.google.firebase.firestore.ListenerRegistration? = null

    init {
        listenToBlockedUsers()
        listenToSavedPosts()
        loadStories()
    }
    
    private fun listenToSavedPosts() {
        db.collection("users").document(currentUserId).collection("saved_posts")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val ids = snapshot.documents.map { it.id }.toSet()
                _savedPostIds.value = ids
            }
    }
    
    private fun listenToBlockedUsers() {
        userProfileListener?.remove()
        userProfileListener = db.collection("users").document(currentUserId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("StoryViewModel", "Listen failed for user profile.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    currentBlockedUsers = profile?.blockedUsers ?: emptyList()
                    updateUiState()
                }
            }
    }
    
    private fun updateUiState() {
        if (rawStories.isNotEmpty()) {
            val filteredStories = rawStories.filter { it.authorId !in currentBlockedUsers }
            _uiState.value = StoryUiState.Success(filteredStories)
        } else {
            if (_uiState.value is StoryUiState.Success) {
                _uiState.value = StoryUiState.Success(emptyList())
            }
        }
    }
    
    fun blockUser(targetUserId: String) {
        viewModelScope.launch {
            try {
                db.collection("users").document(currentUserId).update(
                    "blockedUsers", FieldValue.arrayUnion(targetUserId)
                ).await()
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error blocking user", e)
            }
        }
    }
    
    fun reportStory(storyId: String) {
        viewModelScope.launch {
            try {
                val reportData = hashMapOf(
                    "storyId" to storyId,
                    "reporterId" to currentUserId,
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("reports").add(reportData).await()
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error reporting story", e)
            }
        }
    }

    fun loadStories() {
        _uiState.value = StoryUiState.Loading
        fetchStoriesFromFirestore()
    }

    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    private val _currentComments = MutableStateFlow<List<Comment>>(emptyList())
    val currentComments: StateFlow<List<Comment>> = _currentComments.asStateFlow()
    
    private var commentsListenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    fun loadCommentsForStory(storyId: String) {
        commentsListenerRegistration?.remove()
        commentsListenerRegistration = storiesCollection.document(storyId).collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("StoryViewModel", "Comments listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val commentsList = snapshot.toObjects(Comment::class.java)
                    _currentComments.value = commentsList
                }
            }
    }
    
    fun clearCommentsListener() {
        commentsListenerRegistration?.remove()
        _currentComments.value = emptyList()
    }


    private fun fetchStoriesFromFirestore() {
        _uiState.value = StoryUiState.Loading
        listenerRegistration?.remove()
        commentsListenerRegistration?.remove()
        
        listenerRegistration = storiesCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("StoryViewModel", "Listen failed.", e)
                    _uiState.value = StoryUiState.Error(e.message ?: "Failed to listen for stories")
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    rawStories = snapshot.toObjects(Story::class.java)
                    if (rawStories.isEmpty()) {
                        viewModelScope.launch {
                            seedDummyDataToFirestore()
                        }
                    } else {
                        updateUiState()
                    }
                } else {
                    rawStories = emptyList()
                    updateUiState()
                }
            }
    }
    
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
        commentsListenerRegistration?.remove()
        userProfileListener?.remove()
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

    fun addStory(content: String, onComplete: () -> Unit = {}) {
        if (content.isBlank()) return
        
        viewModelScope.launch {
            try {
                // Fetch profile first
                val profileDoc = db.collection("users").document(currentUserId).get().await()
                var authorName = currentUserName
                var authorHandle = "@user"
                var authorAvatarUrl = ""
                
                if (profileDoc.exists()) {
                    val profile = profileDoc.toObject(UserProfile::class.java)
                    if (profile != null) {
                        authorName = profile.name.ifEmpty { currentUserName }
                        authorHandle = if (profile.nickname.isNotEmpty()) "@${profile.nickname.replace(" ", "_").lowercase()}" else "@user"
                        authorAvatarUrl = profile.avatarUrl
                    }
                }
                
                val newStory = Story(
                    id = UUID.randomUUID().toString(),
                    authorId = currentUserId,
                    authorName = authorName,
                    authorHandle = authorHandle,
                    authorAvatarUrl = authorAvatarUrl,
                    timestamp = System.currentTimeMillis(),
                    content = content,
                    likesCount = 0,
                    commentsCount = 0
                )
                
                storiesCollection.document(newStory.id).set(newStory).await()
                refreshStories()
                onComplete()
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error adding story", e)
            }
        }
    }

    fun toggleBookmark(storyId: String) {
        val isSaved = _savedPostIds.value.contains(storyId)
        val docRef = db.collection("users").document(currentUserId).collection("saved_posts").document(storyId)
        viewModelScope.launch {
            try {
                if (isSaved) {
                    docRef.delete().await()
                } else {
                    docRef.set(mapOf("timestamp" to System.currentTimeMillis())).await()
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error toggling bookmark", e)
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
                        val isLiking = newLikes.contains(currentUserId)
                        val storyRef = storiesCollection.document(storyId)
                        val likeRef = storyRef.collection("likes").document(currentUserId)
                        
                        if (isLiking) {
                            val likeData = hashMapOf(
                                "userId" to currentUserId,
                                "timestamp" to System.currentTimeMillis()
                            )
                            likeRef.set(likeData).await()
                            storyRef.update(
                                "likedByUsers", FieldValue.arrayUnion(currentUserId),
                                "likesCount", FieldValue.increment(1)
                            ).await()
                            
                            if (story.authorId != currentUserId) {
                                val notif = Notification(
                                    targetUserId = story.authorId,
                                    sourceUserId = currentUserId,
                                    sourceUserName = currentUserName,
                                    type = "like",
                                    storyId = storyId
                                )
                                db.collection("notifications").document(notif.id).set(notif)
                            }
                        } else {
                            likeRef.delete().await()
                            storyRef.update(
                                "likedByUsers", FieldValue.arrayRemove(currentUserId),
                                "likesCount", FieldValue.increment(-1)
                            ).await()
                        }
                    } catch (e: Exception) {
                        Log.e("StoryViewModel", "Error toggling like", e)
                    }
                }
            }
        }
    }

    fun setFeedFilter(filter: String) {
        _feedFilter.value = filter
        updateUiState()
    }

    fun addComment(storyId: String, content: String) {
        val newComment = Comment(
            authorId = currentUserId,
            authorName = currentUserName,
            authorHandle = "@user",
            content = content
        )
        viewModelScope.launch {
            try {
                // Add to subcollection
                val commentRef = storiesCollection.document(storyId).collection("comments").document(newComment.id)
                commentRef.set(newComment).await()
                
                // Increment commentsCount in the story document
                storiesCollection.document(storyId).update("commentsCount", FieldValue.increment(1)).await()
                
                // Get the story author to send notification
                val storyDoc = storiesCollection.document(storyId).get().await()
                val storyAuthorId = storyDoc.getString("authorId")
                if (storyAuthorId != null && storyAuthorId != currentUserId) {
                    val notif = Notification(
                        targetUserId = storyAuthorId,
                        sourceUserId = currentUserId,
                        sourceUserName = currentUserName,
                        type = "comment",
                        storyId = storyId,
                        content = content
                    )
                    db.collection("notifications").document(notif.id).set(notif)
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error adding comment", e)
            }
        }
    }

    fun refreshStories() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val snapshot = storiesCollection.orderBy("timestamp", Query.Direction.DESCENDING).get().await()
                rawStories = snapshot.toObjects(Story::class.java)
                updateUiState()
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error refreshing stories", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
