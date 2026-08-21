import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

init_target = """    init {
        loadStories()
    }"""
init_replacement = """    private var currentBlockedUsers: List<String> = emptyList()
    private var rawStories: List<Story> = emptyList()
    private var userProfileListener: com.google.firebase.firestore.ListenerRegistration? = null

    init {
        listenToBlockedUsers()
        loadStories()
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
    }"""

content = content.replace(init_target, init_replacement)

fetch_target = """                if (snapshot != null) {
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
                }"""

fetch_replacement = """                if (snapshot != null) {
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
                }"""
content = content.replace(fetch_target, fetch_replacement)

on_clear_target = """        super.onCleared()
        listenerRegistration?.remove()
        commentsListenerRegistration?.remove()"""
on_clear_replacement = """        super.onCleared()
        listenerRegistration?.remove()
        commentsListenerRegistration?.remove()
        userProfileListener?.remove()"""
content = content.replace(on_clear_target, on_clear_replacement)

with open(path, 'w') as f:
    f.write(content)

print("Updated StoryViewModel for blocking")
