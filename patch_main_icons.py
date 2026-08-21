import re

path = 'app/src/main/java/com/example/MainActivity.kt'
with open(path, 'r') as f:
    content = f.read()

if "import androidx.compose.material.icons.filled.Notifications" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Person", 
        "import androidx.compose.material.icons.filled.Person\nimport androidx.compose.material.icons.filled.Notifications\nimport androidx.compose.material.icons.outlined.Notifications")

with open(path, 'w') as f:
    f.write(content)
