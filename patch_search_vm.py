import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

# Add states for search
state_target = """    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()"""
    
state_replacement = """    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

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
                    .whereLessThanOrEqualTo("content", query + "\\uf8ff")
                    .limit(20)
                    .get().await()
                _searchStoriesResult.value = storiesSnapshot.toObjects(Story::class.java)

                val usersSnapshot = db.collection("users")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\\uf8ff")
                    .limit(20)
                    .get().await()
                _searchUsersResult.value = usersSnapshot.toObjects(UserProfile::class.java)
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error searching", e)
            } finally {
                _isSearching.value = false
            }
        }
    }"""

content = content.replace(state_target, state_replacement)

with open(path, 'w') as f:
    f.write(content)
