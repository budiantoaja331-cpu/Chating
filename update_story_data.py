import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace(
    'val authorHandle: String = "",',
    'val authorHandle: String = "",\n    val authorAvatarUrl: String = "",'
)

with open(path, 'w') as f:
    f.write(content)

print("Updated data classes")
