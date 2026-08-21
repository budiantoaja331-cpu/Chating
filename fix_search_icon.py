import re
path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace("androidx.compose.material.icons.Icons.Filled.androidx.compose.material.icons.filled.Search", "androidx.compose.material.icons.filled.Search")

with open(path, 'w') as f:
    f.write(content)
