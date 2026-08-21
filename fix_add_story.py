import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    fun addStory(content: String) {
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
    }"""

replacement = """    fun addStory(content: String, onComplete: () -> Unit = {}) {
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
    }"""

content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)

print("Updated addStory")
