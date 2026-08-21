import re

path = 'app/src/main/java/com/example/MainActivity.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                ChatRoomScreen(
                    onNavigateToCall = { targetUserId, isVideo ->"""

replacement = """                ChatRoomScreen(
                    onNavigateToProfile = { targetUserId ->
                        navController.navigate("friendProfile/$targetUserId")
                    },
                    onNavigateToCall = { targetUserId, isVideo ->"""

if target in content:
    content = content.replace(target, replacement)
    with open(path, 'w') as f:
        f.write(content)
    print("Updated MainActivity for ChatRoom profile nav")


path2 = 'app/src/main/java/com/example/ChatRoomScreen.kt'
with open(path2, 'r') as f:
    content2 = f.read()

target2 = """    onNavigateBack: () -> Unit,
    onNavigateToCall: (String, Boolean) -> Unit
) {"""

replacement2 = """    onNavigateBack: () -> Unit,
    onNavigateToCall: (String, Boolean) -> Unit,
    onNavigateToProfile: (String) -> Unit = {}
) {"""

content2 = content2.replace(target2, replacement2)

title_target = """                title = { Text(otherUserName, fontWeight = FontWeight.Bold) },"""
title_replacement = """                title = { 
                    TextButton(onClick = { onNavigateToProfile(otherUserId) }) {
                        Text(otherUserName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                },"""
content2 = content2.replace(title_target, title_replacement)

with open(path2, 'w') as f:
    f.write(content2)
print("Updated ChatRoomScreen for profile nav")
