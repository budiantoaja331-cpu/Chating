import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace("import androidx.compose.material.icons.outlined.Person",
                          "import androidx.compose.material.icons.outlined.Person\nimport androidx.compose.material.icons.filled.Notifications")

with open(path, 'w') as f:
    f.write(content)
