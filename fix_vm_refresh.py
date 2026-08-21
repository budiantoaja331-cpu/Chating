import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    fun refreshStories() {
        loadStories()
    }

    fun loadStories() {"""

replacement = """    fun loadStories() {"""

content = content.replace(target, replacement)

# The original refreshStories uses stories directly. Since I added block users,
# I need to update the bottom refreshStories to use rawStories and updateUiState() instead of _uiState.value = StoryUiState.Success(stories)
bottom_refresh_target = """    fun refreshStories() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val snapshot = storiesCollection.orderBy("timestamp", Query.Direction.DESCENDING).get().await()
                val stories = snapshot.toObjects(Story::class.java)
                _uiState.value = StoryUiState.Success(stories)
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error refreshing stories", e)
                // Avoid overriding success state with error on refresh if there's old data,
                // but for simplicity we'll just show the error.
            } finally {
                _isRefreshing.value = false
            }
        }
    }"""
bottom_refresh_replacement = """    fun refreshStories() {
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
    }"""
content = content.replace(bottom_refresh_target, bottom_refresh_replacement)

with open(path, 'w') as f:
    f.write(content)
print("Fixed refreshStories")
