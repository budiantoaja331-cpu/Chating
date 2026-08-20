sed -i 's/onNavigateBack: () -> Unit/onNavigateBack: () -> Unit,\n    onNavigateToCall: (String, Boolean) -> Unit/' app/src/main/java/com/example/ChatRoomScreen.kt

sed -i '/import androidx.compose.material.icons.automirrored.filled.Send/a import androidx.compose.material.icons.filled.Call\nimport androidx.compose.material.icons.filled.Videocam' app/src/main/java/com/example/ChatRoomScreen.kt

sed -i '/colors = TopAppBarDefaults.topAppBarColors/i \                actions = {\n                    IconButton(onClick = { onNavigateToCall(otherUserId, false) }) {\n                        Icon(Icons.Filled.Call, contentDescription = "Voice Call")\n                    }\n                    IconButton(onClick = { onNavigateToCall(otherUserId, true) }) {\n                        Icon(Icons.Filled.Videocam, contentDescription = "Video Call")\n                    }\n                },' app/src/main/java/com/example/ChatRoomScreen.kt
