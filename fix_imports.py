import os

def replace_in_file(filepath, replacements):
    if not os.path.exists(filepath): return
    with open(filepath, 'r') as f:
        content = f.read()
    for old, new in replacements.items():
        content = content.replace(old, new)
    with open(filepath, 'w') as f:
        f.write(content)

replace_in_file('app/src/main/java/com/example/CallHistoryScreen.kt', {
    'import androidx.compose.material.icons.filled.CallMade': '',
    'import androidx.compose.material.icons.filled.CallMissed': '',
    'import androidx.compose.material.icons.filled.CallReceived': ''
})

replace_in_file('app/src/main/java/com/example/ChatListScreen.kt', {
    'import androidx.compose.material.icons.outlined.Chat': ''
})

replace_in_file('app/src/main/java/com/example/MainActivity.kt', {
    'import androidx.compose.material.icons.filled.Chat': '',
    'import androidx.compose.material.icons.outlined.Chat': ''
})

