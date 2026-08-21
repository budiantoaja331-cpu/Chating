import re

path = 'app/src/main/java/com/example/ChatRoomScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target_imports = """import androidx.compose.ui.unit.dp"""
replacement_imports = """import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.MoreVert
import com.example.UserSessionManager
import androidx.compose.ui.platform.LocalContext"""
content = content.replace(target_imports, replacement_imports)

target_state = """    val presenceMap by PresenceManagerInstance.instance.presenceMap.collectAsState()
    val otherUserOnline = presenceMap[otherUserId]?.state == "online\""""
replacement_state = """    val presenceMap by PresenceManagerInstance.instance.presenceMap.collectAsState()
    val otherUserOnline = presenceMap[otherUserId]?.state == "online"
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current"""
content = content.replace(target_state, replacement_state)

target_topbar = """                actions = {
                    IconButton(onClick = { onNavigateToCall(otherUserId, false) }) {
                        Icon(Icons.Filled.Call, contentDescription = "Voice Call")
                    }
                    IconButton(onClick = { onNavigateToCall(otherUserId, true) }) {
                        Icon(Icons.Filled.Videocam, contentDescription = "Video Call")
                    }
                }"""
replacement_topbar = """                actions = {
                    IconButton(onClick = { onNavigateToCall(otherUserId, false) }) {
                        Icon(Icons.Filled.Call, contentDescription = "Voice Call")
                    }
                    IconButton(onClick = { onNavigateToCall(otherUserId, true) }) {
                        Icon(Icons.Filled.Videocam, contentDescription = "Video Call")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Opsi")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Blokir Pengguna") },
                            onClick = { 
                                showMenu = false
                                UserSessionManager.blockUser(otherUserId)
                                android.widget.Toast.makeText(context, "Pengguna diblokir.", android.widget.Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            }
                        )
                    }
                }"""
content = content.replace(target_topbar, replacement_topbar)

with open(path, 'w') as f:
    f.write(content)
