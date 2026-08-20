import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace('androidx.compose.foundation.layout.Modifier.fillMaxSize()', 'Modifier.fillMaxSize()')
content = content.replace('androidx.compose.foundation.layout.Box', 'Box')

if 'import androidx.compose.foundation.layout.Box' not in content:
    content = content.replace('import androidx.compose.foundation.layout.*', 'import androidx.compose.foundation.layout.*\nimport androidx.compose.foundation.layout.Box')
    
with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
