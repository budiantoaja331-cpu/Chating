import re

path = 'app/src/main/java/com/example/CallScreen.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.zIndex.zIndex", "")
content = content.replace(".zIndex(10f)", "")

with open(path, 'w') as f:
    f.write(content)
