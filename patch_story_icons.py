import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = "androidx.compose.material.icons.Icons.Filled.Notifications"
replacement = "Icons.Filled.Notifications"
content = content.replace(target, replacement)

if "import androidx.compose.material.icons.filled.Notifications" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Person", 
                              "import androidx.compose.material.icons.filled.Person\nimport androidx.compose.material.icons.filled.Notifications")

with open(path, 'w') as f:
    f.write(content)
