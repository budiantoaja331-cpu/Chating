import re
path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = "import androidx.compose.material.icons.filled.MoreVert"
replacement = "import androidx.compose.material.icons.filled.MoreVert\nimport androidx.compose.material.icons.filled.Search"
content = content.replace(target, replacement)

content = content.replace("androidx.compose.material.icons.filled.Search", "Icons.Filled.Search")

with open(path, 'w') as f:
    f.write(content)
