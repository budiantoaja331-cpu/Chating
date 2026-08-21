import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

# Add comments state and listener
if 'val currentComments:' not in content:
    listener_code = """
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
"""
    # Insert after listenerRegistration for stories
    content = content.replace('private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null', 
                              'private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null\n' + listener_code)

# Update onCleared to also remove commentsListenerRegistration
content = content.replace('listenerRegistration?.remove()', 'listenerRegistration?.remove()\n        commentsListenerRegistration?.remove()')

# Rewrite addComment
target_add_comment = """    fun addComment(storyId: String, content: String) {
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
    }"""

replacement_add_comment = """    fun addComment(storyId: String, content: String) {
        viewModelScope.launch {
            try {
                // Fetch profile first for updated info
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

                val newComment = Comment(
                    authorId = currentUserId,
                    authorName = authorName,
                    authorHandle = authorHandle,
                    authorAvatarUrl = authorAvatarUrl,
                    content = content
                )
                
                val storyRef = storiesCollection.document(storyId)
                db.runBatch { batch ->
                    val commentRef = storyRef.collection("comments").document(newComment.id)
                    batch.set(commentRef, newComment)
                    batch.update(storyRef, "commentsCount", FieldValue.increment(1))
                }.await()
                
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error adding comment", e)
            }
        }
    }"""

content = content.replace(target_add_comment, replacement_add_comment)

with open(path, 'w') as f:
    f.write(content)

print("Updated StoryViewModel comments")
