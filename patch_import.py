import re
path = 'app/src/main/java/com/example/FriendProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()
if "import androidx.compose.material.icons.filled.Add" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Person", "import androidx.compose.material.icons.filled.Person\nimport androidx.compose.material.icons.filled.Add")
content = content.replace("androidx.compose.material.icons.Icons.Filled.Add", "Icons.Filled.Add")
with open(path, 'w') as f:
    f.write(content)
