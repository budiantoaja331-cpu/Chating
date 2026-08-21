import re

path = 'app/src/main/java/com/example/NearbyScreen.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace(
    'onNavigateToChat: (String, String) -> Unit = { _, _ -> }',
    'onNavigateToProfile: (String) -> Unit = { _ -> }'
)

content = content.replace(
    'NearbyUserCard(user, onNavigateToChat)',
    'NearbyUserCard(user, onNavigateToProfile)'
)

content = content.replace(
    'fun NearbyUserCard(user: NearbyUser, onNavigateToChat: (String, String) -> Unit)',
    'fun NearbyUserCard(user: NearbyUser, onNavigateToProfile: (String) -> Unit)'
)

button_target = """            Button(onClick = { onNavigateToChat(user.id, user.name) }) {
                Text("Chat")
            }"""
button_replacement = """            Button(onClick = { onNavigateToProfile(user.id) }) {
                Text("Profil")
            }"""
content = content.replace(button_target, button_replacement)

with open(path, 'w') as f:
    f.write(content)

print("Updated NearbyScreen")
