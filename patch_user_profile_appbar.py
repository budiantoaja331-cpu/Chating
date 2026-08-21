import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add mutable states for settings menu and dialog
target_states = """    var editNickname by remember { mutableStateOf("") }
    var editAge by remember { mutableStateOf("") }
    var editInterests by remember { mutableStateOf("") }"""

replacement_states = """    var editNickname by remember { mutableStateOf("") }
    var editAge by remember { mutableStateOf("") }
    var editInterests by remember { mutableStateOf("") }
    
    var showMenu by remember { mutableStateOf(false) }
    var showBlockedUsersDialog by remember { mutableStateOf(false) }
    val blockedUserProfiles by viewModel.blockedUserProfiles.collectAsState()
    val blockedUserIds by UserSessionManager.blockedUsers.collectAsState()
    
    LaunchedEffect(showBlockedUsersDialog, blockedUserIds) {
        if (showBlockedUsersDialog) {
            viewModel.loadBlockedUsers(blockedUserIds)
        }
    }"""
content = content.replace(target_states, replacement_states)

# Add MoreVert icon to TopAppBar
target_topbar = """                actions = {
                    if (uiState is UserProfileUiState.Success) {
                        IconButton(onClick = {"""

replacement_topbar = """                actions = {
                    if (uiState is UserProfileUiState.Success) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(androidx.compose.material.icons.filled.Settings, contentDescription = "Pengaturan")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Daftar Blokir") },
                                onClick = { 
                                    showMenu = false
                                    showBlockedUsersDialog = true
                                }
                            )
                        }
                        IconButton(onClick = {"""
content = content.replace(target_topbar, replacement_topbar)

with open(path, 'w') as f:
    f.write(content)
