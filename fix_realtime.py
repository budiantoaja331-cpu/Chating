import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

if 'import com.google.firebase.firestore.FieldValue' not in content:
    content = content.replace('import com.google.firebase.firestore.FirebaseFirestore', 'import com.google.firebase.firestore.FirebaseFirestore\nimport com.google.firebase.firestore.FieldValue')

target_fetch = """    private fun fetchStoriesFromFirestore() {
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
    }"""

replacement_fetch = """    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    private fun fetchStoriesFromFirestore() {
        _uiState.value = StoryUiState.Loading
        listenerRegistration?.remove()
        
        listenerRegistration = storiesCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("StoryViewModel", "Listen failed.", e)
                    _uiState.value = StoryUiState.Error(e.message ?: "Failed to listen for stories")
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val stories = snapshot.toObjects(Story::class.java)
                    if (stories.isEmpty()) {
                        viewModelScope.launch {
                            seedDummyDataToFirestore()
                        }
                    } else {
                        _uiState.value = StoryUiState.Success(stories)
                    }
                } else {
                    _uiState.value = StoryUiState.Success(emptyList())
                }
            }
    }
    
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }"""

content = content.replace(target_fetch, replacement_fetch)

target_toggle = """                // Update Firestore
                viewModelScope.launch {
                    try {
                        storiesCollection.document(storyId).set(updatedStory).await()
                    } catch (e: Exception) {
                        Log.e("StoryViewModel", "Error toggling like", e)
                    }
                }"""

replacement_toggle = """                // Update Firestore
                viewModelScope.launch {
                    try {
                        val isLiking = newLikes.contains(currentUserId)
                        storiesCollection.document(storyId).update(
                            "likedByUsers", if (isLiking) FieldValue.arrayUnion(currentUserId) else FieldValue.arrayRemove(currentUserId),
                            "likesCount", if (isLiking) FieldValue.increment(1) else FieldValue.increment(-1)
                        ).await()
                    } catch (e: Exception) {
                        Log.e("StoryViewModel", "Error toggling like", e)
                    }
                }"""

content = content.replace(target_toggle, replacement_toggle)

with open(path, 'w') as f:
    f.write(content)

print("Updated realtime")
