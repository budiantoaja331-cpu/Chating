import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add context
if "val context = androidx.compose.ui.platform.LocalContext.current" not in content:
    target_context = "    val savedPostIds by storyViewModel.savedPostIds.collectAsState()"
    replacement_context = "    val savedPostIds by storyViewModel.savedPostIds.collectAsState()\n    val context = androidx.compose.ui.platform.LocalContext.current"
    content = content.replace(target_context, replacement_context)

target = """                                    StoryCard(
                                        story = story,
                                        currentUserId = storyViewModel.currentUserId,
                                        isBookmarked = savedPostIds.contains(story.id),
                                        onLikeClick = { storyViewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { storyViewModel.toggleBookmark(story.id) },
                                        onBlockClick = { storyViewModel.blockUser(story.authorId) },
                                        onReportClick = { storyViewModel.reportStory(story.id) }
                                    )"""
replacement = """                                    StoryCard(
                                        story = story,
                                        currentUserId = storyViewModel.currentUserId,
                                        isBookmarked = savedPostIds.contains(story.id),
                                        onLikeClick = { storyViewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { storyViewModel.toggleBookmark(story.id) },
                                        onBlockClick = { storyViewModel.blockUser(story.authorId) },
                                        onReportClick = { storyViewModel.reportStory(story.id) },
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
content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)
