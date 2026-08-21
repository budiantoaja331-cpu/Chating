import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                                    StoryCard(
                                        story = story,
                                        currentUserId = storyViewModel.currentUserId,
                                        onLikeClick = { storyViewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { storyViewModel.toggleBookmark(story.id) }
                                    )"""
replacement = """                                    StoryCard(
                                        story = story,
                                        currentUserId = storyViewModel.currentUserId,
                                        onLikeClick = { storyViewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { storyViewModel.toggleBookmark(story.id) },
                                        onBlockClick = { storyViewModel.blockUser(story.authorId) }
                                    )"""
content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)
print("Updated UserProfileScreen call")
