sed -i 's/onCommentClick = { selectedStoryForComments = story }/onCommentClick = { selectedStoryForComments = story },\
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) }/' app/src/main/java/com/example/StoryScreen.kt
