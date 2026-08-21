import re

path = 'app/src/main/java/com/example/FriendProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target_imports = """import coil.compose.AsyncImage"""
replacement_imports = """import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.MoreVert
import com.example.UserSessionManager
import androidx.compose.ui.platform.LocalContext"""
content = content.replace(target_imports, replacement_imports)

target_state = """    val viewModel: FriendProfileViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()"""
replacement_state = """    val viewModel: FriendProfileViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current"""
content = content.replace(target_state, replacement_state)

target_topbar = """                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }"""
replacement_topbar = """                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
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
                                UserSessionManager.blockUser(friendId)
                                android.widget.Toast.makeText(context, "Pengguna diblokir.", android.widget.Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            }
                        )
                    }
                }"""
content = content.replace(target_topbar, replacement_topbar)

with open(path, 'w') as f:
    f.write(content)
