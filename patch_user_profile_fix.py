import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add the missing Settings icon import
if "import androidx.compose.material.icons.filled.Settings" not in content:
    content = content.replace(
        "import androidx.compose.material.icons.filled.Check",
        "import androidx.compose.material.icons.filled.Check\nimport androidx.compose.material.icons.filled.Settings"
    )

dialog_code = """        if (showBlockedUsersDialog) {
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

# Remove dialog_code from inside LazyColumn
content = content.replace(dialog_code, "")

# Append dialog_code right before the last closing brace of UserProfileScreen function
target_end = """        }
    }
}

@Composable
fun StoryCard("""

replacement_end = """        }
    }

""" + dialog_code + """
}

@Composable
fun StoryCard("""

if target_end in content:
    content = content.replace(target_end, replacement_end)
else:
    # Let's find another anchor at the bottom of the function
    target_end2 = """        }
    }
}"""
    content = content.replace(target_end2, """        }
    }
""" + dialog_code + """}""")

with open(path, 'w') as f:
    f.write(content)
