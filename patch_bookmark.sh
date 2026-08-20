sed -i '/fun toggleLike(storyId: String, currentLikes: List<String>) {/i\
    fun toggleBookmark(storyId: String) {\
        val index = dummyStories.indexOfFirst { it.id == storyId }\
        if (index != -1) {\
            val story = dummyStories[index]\
            val newBookmarks = story.bookmarkedByUsers.toMutableList()\
            if (newBookmarks.contains(currentUserId)) {\
                newBookmarks.remove(currentUserId)\
            } else {\
                newBookmarks.add(currentUserId)\
            }\
            dummyStories[index] = story.copy(\
                bookmarkedByUsers = newBookmarks\
            )\
            _uiState.value = StoryUiState.Success(dummyStories.toList())\
        }\
    }\
' app/src/main/java/com/example/StoryViewModel.kt
