import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """        // My Posts or Saved Posts
"""

replacement = """        // My Posts or Saved Posts

        if (showBlockedUsersDialog) {
            AlertDialog(
                onDismissRequest = { showBlockedUsersDialog = false },
                title = { Text("Daftar Blokir") },
                text = {
                    if (blockedUserProfiles.isEmpty()) {
                        Text("Tidak ada pengguna yang diblokir.")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(blockedUserProfiles) { profile ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (profile.avatarUrl.isNotEmpty()) {
                                            AsyncImage(
                                                model = profile.avatarUrl,
                                                contentDescription = "Avatar",
                                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(profile.name, fontWeight = FontWeight.SemiBold)
                                    }
                                    TextButton(onClick = { viewModel.unblockUser(profile.id) }) {
                                        Text("Buka Blokir", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showBlockedUsersDialog = false }) {
                        Text("Tutup")
                    }
                }
            )
        }
"""
content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)
