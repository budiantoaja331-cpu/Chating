import re

path1 = 'app/src/main/java/com/example/MainActivity.kt'
with open(path1, 'r') as f:
    c1 = f.read()

if 'import androidx.compose.material.icons.filled.Add' not in c1:
    c1 = c1.replace('import androidx.compose.material.icons.filled.Person', 'import androidx.compose.material.icons.filled.Person\nimport androidx.compose.material.icons.filled.Add')

with open(path1, 'w') as f:
    f.write(c1)

path2 = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path2, 'r') as f:
    c2 = f.read()

if 'import coil.compose.AsyncImage' not in c2:
    c2 = c2.replace('import androidx.lifecycle.viewmodel.compose.viewModel', 'import androidx.lifecycle.viewmodel.compose.viewModel\nimport coil.compose.AsyncImage')

if 'import androidx.compose.ui.layout.ContentScale' not in c2:
    c2 = c2.replace('import androidx.compose.ui.Alignment', 'import androidx.compose.ui.Alignment\nimport androidx.compose.ui.layout.ContentScale')

with open(path2, 'w') as f:
    f.write(c2)

print("Fixed imports")
