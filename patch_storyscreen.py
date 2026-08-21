import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# 1. Update StoryCard parameters
sig_target = """fun StoryCard(
    story: Story,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit = {},
    onBlockClick: () -> Unit = {}
)"""
sig_replacement = """fun StoryCard(
    story: Story,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit = {},
    onBlockClick: () -> Unit = {},
    onReportClick: () -> Unit = {}
)"""
content = content.replace(sig_target, sig_replacement)

# 2. Update DropdownMenu
dropdown_target = """                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Blokir Pengguna") },
                                onClick = { 
                                    expanded = false
                                    onBlockClick() 
                                }
                            )
                        }"""
dropdown_replacement = """                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Laporkan Postingan") },
                                onClick = {
                                    expanded = false
                                    onReportClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Blokir Pengguna") },
                                onClick = { 
                                    expanded = false
                                    onBlockClick() 
                                }
                            )
                        }"""
content = content.replace(dropdown_target, dropdown_replacement)

# 3. Update StoryCard invocation
invoke_target = """                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId,
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) }
                                )"""
invoke_replacement = """                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId,
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) },
                                    onReportClick = { viewModel.reportStory(story.id) }
                                )"""
content = content.replace(invoke_target, invoke_replacement)

with open(path, 'w') as f:
    f.write(content)
print("Added Report functionality to StoryScreen")
