import re

path = 'app/src/main/java/com/example/FriendProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target1 = """    val uiState by viewModel.uiState.collectAsState()"""
replacement1 = """    val uiState by viewModel.uiState.collectAsState()
    val isFollowing by viewModel.isFollowing.collectAsState()"""
content = content.replace(target1, replacement1)

target2 = """                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Button(
                                    onClick = { onNavigateToChat(profile.id, profile.name) },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                ) {"""
replacement2 = """                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Button(
                                    onClick = { viewModel.toggleFollow() },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isFollowing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isFollowing) Icons.Filled.Person else androidx.compose.material.icons.Icons.Filled.Add, 
                                        contentDescription = "Follow", 
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isFollowing) "Mengikuti" else "Ikuti")
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                OutlinedButton(
                                    onClick = { onNavigateToChat(profile.id, profile.name) },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                ) {"""
content = content.replace(target2, replacement2)

with open(path, 'w') as f:
    f.write(content)
