import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

# Add filter state
target1 = """    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()"""
replacement1 = """    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private val _feedFilter = MutableStateFlow("All")
    val feedFilter: StateFlow<String> = _feedFilter.asStateFlow()
    
    private var currentUserFollowing: List<String> = emptyList()"""
content = content.replace(target1, replacement1)

# Listen to currentUser profile to update currentUserFollowing
target2 = """    init {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            currentUserId = uid
            UserSessionManager.startListening(uid)
        }
        
        // Listen to blocked users from UserSessionManager
        viewModelScope.launch {
            UserSessionManager.blockedUsers.collect { blocked ->
                currentBlockedUsers = blocked
                updateUiState()
            }
        }"""
replacement2 = """    init {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            currentUserId = uid
            UserSessionManager.startListening(uid)
            
            // Listen to following list
            viewModelScope.launch {
                db.collection("users").document(uid).addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    if (snapshot != null && snapshot.exists()) {
                        val profile = snapshot.toObject(UserProfile::class.java)
                        currentUserFollowing = profile?.following ?: emptyList()
                        updateUiState()
                    }
                }
            }
        }
        
        // Listen to blocked users from UserSessionManager
        viewModelScope.launch {
            UserSessionManager.blockedUsers.collect { blocked ->
                currentBlockedUsers = blocked
                updateUiState()
            }
        }"""
content = content.replace(target2, replacement2)

# Update setFeedFilter
target_add = """    fun addComment(storyId: String, content: String) {"""
replacement_add = """    fun setFeedFilter(filter: String) {
        _feedFilter.value = filter
        updateUiState()
    }

    fun addComment(storyId: String, content: String) {"""
content = content.replace(target_add, replacement_add)

# Update updateUiState
target3 = """    private fun updateUiState() {
        if (rawStories.isNotEmpty()) {
            val filteredStories = rawStories.filter { it.authorId !in currentBlockedUsers }
            _uiState.value = StoryUiState.Success(filteredStories)
        } else {
            _uiState.value = StoryUiState.Success(emptyList())
        }
    }"""
replacement3 = """    private fun updateUiState() {
        if (rawStories.isNotEmpty()) {
            var filteredStories = rawStories.filter { it.authorId !in currentBlockedUsers }
            when (_feedFilter.value) {
                "Following" -> {
                    filteredStories = filteredStories.filter { it.authorId in currentUserFollowing || it.authorId == currentUserId }
                }
                "Trending" -> {
                    filteredStories = filteredStories.sortedByDescending { it.likesCount }
                }
            }
            _uiState.value = StoryUiState.Success(filteredStories)
        } else {
            _uiState.value = StoryUiState.Success(emptyList())
        }
    }"""
content = content.replace(target3, replacement3)

with open(path, 'w') as f:
    f.write(content)
