import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                                    StoryCard(
                                        story = story,
                                        currentUserId = viewModel.currentUserId,
                                        isBookmarked = savedPostIds.contains(story.id),
                                        onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                        onBlockClick = { viewModel.blockUser(story.authorId) },
                                        onReportClick = { viewModel.reportStory(story.id) }
                                    )"""

replacement = """                                    StoryCard(
                                        story = story,
                                        currentUserId = viewModel.currentUserId,
                                        isBookmarked = savedPostIds.contains(story.id),
                                        onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                        onBlockClick = { viewModel.blockUser(story.authorId) },
                                        onReportClick = { viewModel.reportStory(story.id) },
                                        onShareClick = {
                                            val sendIntent: android.content.Intent = android.content.Intent().apply {
                                                action = android.content.Intent.ACTION_SEND
                                                putExtra(android.content.Intent.EXTRA_TEXT, "Lihat postingan dari ${story.authorName} di Chatmicall: \"${story.content}\"")
                                                type = "text/plain"
                                            }
                                            val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                            context.startActivity(shareIntent)
                                        }
                                    )"""

if target in content:
    content = content.replace(target, replacement)
else:
    print("TARGET NOT FOUND IN STORY SCREEN")

# Also need to add val context = LocalContext.current if not present
if "val context = LocalContext.current" not in content:
    target_context = "    val savedPostIds by viewModel.savedPostIds.collectAsState()"
    replacement_context = "    val savedPostIds by viewModel.savedPostIds.collectAsState()\n    val context = androidx.compose.ui.platform.LocalContext.current"
    content = content.replace(target_context, replacement_context)

with open(path, 'w') as f:
    f.write(content)
