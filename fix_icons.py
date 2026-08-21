import re
path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace("import Icons.Filled.Search", "import androidx.compose.material.icons.filled.Search")
content = content.replace("Icon(Icons.Filled.Search, contentDescription = \"Search\")", "Icon(androidx.compose.material.icons.filled.Search, contentDescription = \"Search\")")

with open(path, 'w') as f:
    f.write(content)
