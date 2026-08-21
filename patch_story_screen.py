import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add context
if "val context = androidx.compose.ui.platform.LocalContext.current" not in content:
    target_context = "    val savedPostIds by viewModel.savedPostIds.collectAsState()"
    replacement_context = "    val savedPostIds by viewModel.savedPostIds.collectAsState()\n    val context = androidx.compose.ui.platform.LocalContext.current"
    content = content.replace(target_context, replacement_context)

# Search stories usage
target1 = """                                StoryCard(
                                    story = story,
                                    currentUserId = currentUserId,
                                    isBookmarked = savedPostIds.contains(story.id),
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) },
                                    onReportClick = { viewModel.reportStory(story.id) }
                                )"""
replacement1 = """                                StoryCard(
                                    story = story,
                                    currentUserId = currentUserId,
                                    isBookmarked = savedPostIds.contains(story.id),
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) },
                                    onReportClick = { viewModel.reportStory(story.id) },
                                    onShareClick = {
                                        val sendIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_TEXT, "Lihat postingan dari ${story.authorName} di Chatmicall: \\"${story.content}\\"")
                                            type = "text/plain"
                                        }
                                        val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                        context.startActivity(shareIntent)
                                    }
                                )"""
content = content.replace(target1, replacement1)

# Main stories usage
target2 = """                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId,
                                    isBookmarked = savedPostIds.contains(story.id),
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) },
                                    onReportClick = { viewModel.reportStory(story.id) }
                                )"""
replacement2 = """                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId,
                                    isBookmarked = savedPostIds.contains(story.id),
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) },
                                    onReportClick = { viewModel.reportStory(story.id) },
                                    onShareClick = {
                                        val sendIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_TEXT, "Lihat postingan dari ${story.authorName} di Chatmicall: \\"${story.content}\\"")
                                            type = "text/plain"
                                        }
                                        val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                        context.startActivity(shareIntent)
                                    }
                                )"""
content = content.replace(target2, replacement2)

with open(path, 'w') as f:
    f.write(content)
