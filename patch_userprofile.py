import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

invoke_target = """                                    StoryCard(
                                        story = story,
                                        currentUserId = storyViewModel.currentUserId,
                                        onLikeClick = { storyViewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { storyViewModel.toggleBookmark(story.id) },
                                        onBlockClick = { storyViewModel.blockUser(story.authorId) }
                                    )"""
invoke_replacement = """                                    StoryCard(
                                        story = story,
                                        currentUserId = storyViewModel.currentUserId,
                                        onLikeClick = { storyViewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { storyViewModel.toggleBookmark(story.id) },
                                        onBlockClick = { storyViewModel.blockUser(story.authorId) },
                                        onReportClick = { storyViewModel.reportStory(story.id) }
                                    )"""
content = content.replace(invoke_target, invoke_replacement)

with open(path, 'w') as f:
    f.write(content)
print("Added Report functionality to UserProfileScreen")
