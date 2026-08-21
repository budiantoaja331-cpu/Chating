import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target_card = """fun StoryCard(
    story: Story,
    currentUserId: String,
    isBookmarked: Boolean = false,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit = {},
    onBlockClick: () -> Unit = {},
    onReportClick: () -> Unit = {}
) {"""

replacement_card = """fun StoryCard(
    story: Story,
    currentUserId: String,
    isBookmarked: Boolean = false,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit = {},
    onBlockClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {"""
content = content.replace(target_card, replacement_card)

target_btn = """                // Share Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {"""

replacement_btn = """                // Share Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onShareClick() }
                        .padding(4.dp)
                ) {"""
content = content.replace(target_btn, replacement_btn)

with open(path, 'w') as f:
    f.write(content)
