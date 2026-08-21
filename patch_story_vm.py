import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    fun addComment(storyId: String, content: String) {
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

replacement = """    fun addComment(storyId: String, content: String) {
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
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error adding comment", e)
            }
        }
    }"""

content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)
