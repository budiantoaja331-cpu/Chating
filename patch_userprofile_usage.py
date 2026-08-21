import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add savedPostIds state
state_target = """    val storyUiState by storyViewModel.uiState.collectAsState()
    var isEditing by remember { mutableStateOf(false) }"""
state_replacement = """    val storyUiState by storyViewModel.uiState.collectAsState()
    val savedPostIds by storyViewModel.savedPostIds.collectAsState()
    var isEditing by remember { mutableStateOf(false) }"""
content = content.replace(state_target, state_replacement)

# Update filtering logic
filter_target = """                            val filteredStories = if (selectedTab == 0) {
                                allStories.filter { it.authorId == storyViewModel.currentUserId }
                            } else {
                                allStories.filter { it.bookmarkedByUsers.contains(storyViewModel.currentUserId) }
                            }"""
filter_replacement = """                            val filteredStories = if (selectedTab == 0) {
                                allStories.filter { it.authorId == storyViewModel.currentUserId }
                            } else {
                                allStories.filter { savedPostIds.contains(it.id) }
                            }"""
content = content.replace(filter_target, filter_replacement)

# Update StoryCard invocation
invoke_target = """                                    StoryCard(
                                        story = story,
                                        currentUserId = storyViewModel.currentUserId,
                                        onLikeClick = { storyViewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { storyViewModel.toggleBookmark(story.id) },
                                        onBlockClick = { storyViewModel.blockUser(story.authorId) },
                                        onReportClick = { storyViewModel.reportStory(story.id) }
                                    )"""
invoke_replacement = """                                    StoryCard(
                                        story = story,
                                        currentUserId = storyViewModel.currentUserId,
                                        isBookmarked = savedPostIds.contains(story.id),
                                        onLikeClick = { storyViewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { storyViewModel.toggleBookmark(story.id) },
                                        onBlockClick = { storyViewModel.blockUser(story.authorId) },
                                        onReportClick = { storyViewModel.reportStory(story.id) }
                                    )"""
content = content.replace(invoke_target, invoke_replacement)

with open(path, 'w') as f:
    f.write(content)
