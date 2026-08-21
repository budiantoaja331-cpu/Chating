import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

sig_target = """fun StoryCard(
    story: Story,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit = {},
    onBlockClick: () -> Unit = {},
    onReportClick: () -> Unit = {}
)"""
sig_replacement = """fun StoryCard(
    story: Story,
    currentUserId: String,
    isBookmarked: Boolean = false,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit = {},
    onBlockClick: () -> Unit = {},
    onReportClick: () -> Unit = {}
)"""
content = content.replace(sig_target, sig_replacement)

bool_target = """val isBookmarked = story.bookmarkedByUsers.contains(currentUserId)"""
bool_replacement = """// val isBookmarked handled by param"""
content = content.replace(bool_target, bool_replacement)

with open(path, 'w') as f:
    f.write(content)
