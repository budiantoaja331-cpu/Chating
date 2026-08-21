import re

path = 'app/src/main/java/com/example/ChatRoomScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()"""
replacement = """    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val presenceMap by PresenceManagerInstance.instance.presenceMap.collectAsState()
    val otherUserOnline = presenceMap[otherUserId]?.state == "online\""""
content = content.replace(target, replacement)

target2 = """                title = { 
                    TextButton(onClick = { onNavigateToProfile(otherUserId) }) {
                        Text(otherUserName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                },"""
replacement2 = """                title = { 
                    TextButton(onClick = { onNavigateToProfile(otherUserId) }) {
                        Column {
                            Text(otherUserName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            if (otherUserOnline) {
                                Text("Online", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                            } else {
                                Text("Offline", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                },"""
content = content.replace(target2, replacement2)

with open(path, 'w') as f:
    f.write(content)
