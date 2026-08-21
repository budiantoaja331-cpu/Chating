import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId, // Should match ViewModel
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) }
                                )"""
replacement = """                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId,
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) }
                                )"""
content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)

print("Updated call")
