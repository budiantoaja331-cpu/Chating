import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

# Add savedPostIds state
state_target = """    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()"""
state_replacement = """    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private val _savedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val savedPostIds: StateFlow<Set<String>> = _savedPostIds.asStateFlow()"""
content = content.replace(state_target, state_replacement)

# Add listenToSavedPosts in init
init_target = """    init {
        listenToBlockedUsers()
        loadStories()
    }"""
init_replacement = """    init {
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
    }"""
content = content.replace(init_target, init_replacement)

# Replace toggleBookmark
toggle_target = """    fun toggleBookmark(storyId: String) {
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
    }"""
toggle_replacement = """    fun toggleBookmark(storyId: String) {
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
    }"""
content = content.replace(toggle_target, toggle_replacement)

with open(path, 'w') as f:
    f.write(content)
print("Updated StoryViewModel with private saved posts")
