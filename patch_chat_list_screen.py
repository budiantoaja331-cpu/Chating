import re

path = 'app/src/main/java/com/example/ChatListScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add import for PresenceManagerInstance
if "import com.example.PresenceManagerInstance" not in content:
    content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport com.example.PresenceManagerInstance\nimport androidx.compose.ui.graphics.Color\nimport androidx.compose.foundation.border")

target = """    val uiState by viewModel.uiState.collectAsState()
    val currentUserId = viewModel.currentUserId"""
replacement = """    val uiState by viewModel.uiState.collectAsState()
    val currentUserId = viewModel.currentUserId
    val presenceMap by PresenceManagerInstance.instance.presenceMap.collectAsState()"""
content = content.replace(target, replacement)

target2 = """                                ChatChannelItem(
                                    channel = channel,
                                    otherUserName = otherUserName,
                                    onClick = { onNavigateToChat(otherUserId, otherUserName) }
                                )"""
replacement2 = """                                val isOnline = presenceMap[otherUserId]?.state == "online"
                                ChatChannelItem(
                                    channel = channel,
                                    otherUserName = otherUserName,
                                    isOnline = isOnline,
                                    onClick = { onNavigateToChat(otherUserId, otherUserName) }
                                )"""
content = content.replace(target2, replacement2)

target3 = """@Composable
fun ChatChannelItem(channel: ChatChannel, otherUserName: String, onClick: () -> Unit) {"""
replacement3 = """@Composable
fun ChatChannelItem(channel: ChatChannel, otherUserName: String, isOnline: Boolean = false, onClick: () -> Unit) {"""
content = content.replace(target3, replacement3)

target4 = """        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Avatar",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }"""
replacement4 = """        Box(
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Avatar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            if (isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }"""
content = content.replace(target4, replacement4)

with open(path, 'w') as f:
    f.write(content)
