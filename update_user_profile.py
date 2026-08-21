import re

path = 'app/src/main/java/com/example/UserProfileViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace(
    'val isProfileComplete: Boolean = false',
    'val isProfileComplete: Boolean = false,\n    val blockedUsers: List<String> = emptyList()'
)

with open(path, 'w') as f:
    f.write(content)

print("Updated UserProfile")
