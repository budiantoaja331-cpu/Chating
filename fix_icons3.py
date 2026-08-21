import re
path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace("leadingIcon = { Icon(androidx.compose.material.icons.filled.Search, contentDescription = \"Search\") },", "leadingIcon = { Icon(Icons.Filled.Search, contentDescription = \"Search\") },")

with open(path, 'w') as f:
    f.write(content)
