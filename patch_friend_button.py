import re

path = 'app/src/main/java/com/example/FriendProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                                Button(
                                    onClick = { onNavigateToChat(profile.id, profile.name) },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                ) {
                                    Icon(Icons.Filled.Chat, contentDescription = "Kirim Pesan", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Kirim Pesan")
                                }"""
replacement = """                                Button(
                                    onClick = { onNavigateToChat(profile.id, profile.name) },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                ) {
                                    Icon(Icons.Filled.Chat, contentDescription = "Kirim Pesan", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Kirim Pesan")
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                OutlinedButton(
                                    onClick = { 
                                        UserSessionManager.blockUser(friendId)
                                        android.widget.Toast.makeText(context, "Pengguna diblokir.", android.widget.Toast.LENGTH_SHORT).show()
                                        onNavigateBack()
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Blokir Pengguna", fontWeight = FontWeight.Bold)
                                }"""

content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)
