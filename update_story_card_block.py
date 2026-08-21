import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Make sure Icons.Filled.MoreVert is imported
if 'import androidx.compose.material.icons.filled.MoreVert' not in content:
    content = content.replace('import androidx.compose.material.icons.filled.Add', 'import androidx.compose.material.icons.filled.Add\nimport androidx.compose.material.icons.filled.MoreVert')

# Update StoryCard signature
target_sig = """@Composable
fun StoryCard(story: Story, currentUserId: String, onLikeClick: () -> Unit, onCommentClick: () -> Unit, onBookmarkClick: () -> Unit = {}) {"""
replacement_sig = """@Composable
fun StoryCard(
    story: Story,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit = {},
    onBlockClick: () -> Unit = {}
) {"""
content = content.replace(target_sig, replacement_sig)

# Update StoryScreen call to StoryCard
target_call = """                                    StoryCard(
                                        story = story,
                                        onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { viewModel.toggleBookmark(story.id) }
                                    )"""
replacement_call = """                                    StoryCard(
                                        story = story,
                                        currentUserId = viewModel.currentUserId,
                                        onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                        onBlockClick = { viewModel.blockUser(story.authorId) }
                                    )"""
content = content.replace(target_call, replacement_call)

# Insert the More Options menu
target_header_end = """                    Text(
                        text = story.formattedTime,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }"""

replacement_header_end = """                    Text(
                        text = story.formattedTime,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (story.authorId != currentUserId) {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Pilihan lainnya")
                        }
                        DropdownMenu(
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
                        }
                    }
                }
            }"""
content = content.replace(target_header_end, replacement_header_end)

with open(path, 'w') as f:
    f.write(content)

print("Updated StoryScreen for block button")
