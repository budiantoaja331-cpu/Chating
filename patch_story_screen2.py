import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target_params = """fun StoryScreen(
    viewModel: StoryViewModel = viewModel(),
    onNavigateToCreatePost: () -> Unit = {}
) {"""

replacement_params = """fun StoryScreen(
    viewModel: StoryViewModel = viewModel(),
    onNavigateToCreatePost: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {"""
content = content.replace(target_params, replacement_params)

target_actions = """                actions = {
                    IconButton(onClick = { /* TODO: Navigate to Profile */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    }
                }"""

replacement_actions = """                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Notifications,
                            contentDescription = "Notifikasi"
                        )
                    }
                }"""
content = content.replace(target_actions, replacement_actions)

with open(path, 'w') as f:
    f.write(content)
