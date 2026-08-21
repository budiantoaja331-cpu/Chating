import re
path = 'app/src/main/java/com/example/NotificationScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                } else if (notification.type == "comment") {
                    Icon(Icons.Filled.ChatBubble, contentDescription = "Commented", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("mengomentari postingan Anda", style = MaterialTheme.typography.bodyMedium)
                }
            }"""
replacement = """                } else if (notification.type == "comment") {
                    Icon(Icons.Filled.ChatBubble, contentDescription = "Commented", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("mengomentari postingan Anda", style = MaterialTheme.typography.bodyMedium)
                } else if (notification.type == "visit") {
                    Icon(Icons.Filled.Person, contentDescription = "Visited", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("mengunjungi profil Anda", style = MaterialTheme.typography.bodyMedium)
                }
            }"""
content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)
