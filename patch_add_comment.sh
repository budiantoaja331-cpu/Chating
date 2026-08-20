sed -i '/fun refreshStories()/i\
    fun addComment(storyId: String, content: String) {\
        val index = dummyStories.indexOfFirst { it.id == storyId }\
        if (index != -1) {\
            val story = dummyStories[index]\
            val newComment = Comment(\
                authorId = currentUserId,\
                authorName = currentUserName,\
                authorHandle = "@user",\
                content = content\
            )\
            val updatedComments = story.comments + newComment\
            dummyStories[index] = story.copy(\
                comments = updatedComments,\
                commentsCount = updatedComments.size\
            )\
            _uiState.value = StoryUiState.Success(dummyStories.toList())\
        }\
    }\
' app/src/main/java/com/example/StoryViewModel.kt
