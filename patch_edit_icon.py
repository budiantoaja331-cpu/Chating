import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """Icon(
                                imageVector = if (isEditing) Icons.Filled.Edit else Icons.Filled.Edit,
                                contentDescription = if (isEditing) "Simpan Profil" else "Edit Profil",
                                tint = if (isEditing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )"""

replacement = """Icon(
                                imageVector = if (isEditing) androidx.compose.material.icons.filled.Check else Icons.Filled.Edit,
                                contentDescription = if (isEditing) "Simpan Profil" else "Edit Profil",
                                tint = if (isEditing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )"""

content = content.replace(target, replacement)

# ensure Icons.Filled.Check import is either imported or we use fully qualified.
# fully qualified is easier.

with open(path, 'w') as f:
    f.write(content)
