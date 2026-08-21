import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add savedPostIds state
state_target = """    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val currentUserId = viewModel.currentUserId"""
state_replacement = """    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val savedPostIds by viewModel.savedPostIds.collectAsState()
    val currentUserId = viewModel.currentUserId"""
content = content.replace(state_target, state_replacement)

# Update StoryCard invocation
invoke_target = """                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId,
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) },
                                    onReportClick = { viewModel.reportStory(story.id) }
                                )"""
invoke_replacement = """                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId,
                                    isBookmarked = savedPostIds.contains(story.id),
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) },
                                    onReportClick = { viewModel.reportStory(story.id) }
                                )"""
content = content.replace(invoke_target, invoke_replacement)

with open(path, 'w') as f:
    f.write(content)
