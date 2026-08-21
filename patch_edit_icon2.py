import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """import androidx.compose.material.icons.filled.Person"""
replacement = """import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Check"""
content = content.replace(target, replacement)

target2 = """imageVector = if (isEditing) androidx.compose.material.icons.filled.Check else Icons.Filled.Edit"""
replacement2 = """imageVector = if (isEditing) Icons.Filled.Check else Icons.Filled.Edit"""
content = content.replace(target2, replacement2)

with open(path, 'w') as f:
    f.write(content)
