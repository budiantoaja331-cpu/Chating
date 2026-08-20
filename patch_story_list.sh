sed -i 's/val currentUserId = viewModel.currentUserId/val currentUserId = viewModel.currentUserId\
    var selectedStoryForComments: Story? by remember { mutableStateOf(null) }/' app/src/main/java/com/example/StoryScreen.kt

sed -i 's/onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) }/onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },\
                                    onCommentClick = { selectedStoryForComments = story }/' app/src/main/java/com/example/StoryScreen.kt
