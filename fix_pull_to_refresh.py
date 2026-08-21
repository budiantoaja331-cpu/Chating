import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                is StoryUiState.Success -> {
                    val stories = (uiState as StoryUiState.Success).stories
                    if (stories.isEmpty()) {
                        Text(
                            text = "Belum ada postingan. Jadilah yang pertama!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 64.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(stories, key = { it.id }) { story ->
                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId,
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) }
                                )
                            }
                        }
                    }
                }"""

replacement = """                is StoryUiState.Success -> {
                    val stories = (uiState as StoryUiState.Success).stories
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (stories.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Belum ada postingan. Jadilah yang pertama!",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(stories, key = { it.id }) { story ->
                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId,
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) }
                                )
                            }
                        }
                    }
                }"""
content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)
print("Updated StoryScreen for better pull-to-refresh")
