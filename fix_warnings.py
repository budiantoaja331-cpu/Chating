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
    'import androidx.compose.material.icons.Icons': 'import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.automirrored.filled.CallMade\nimport androidx.compose.material.icons.automirrored.filled.CallMissed\nimport androidx.compose.material.icons.automirrored.filled.CallReceived',
    'Icons.AutoMirrored.Filled.CallMissed': 'Icons.AutoMirrored.Filled.CallMissed',
    'Icons.AutoMirrored.Filled.CallMade': 'Icons.AutoMirrored.Filled.CallMade',
    'Icons.AutoMirrored.Filled.CallReceived': 'Icons.AutoMirrored.Filled.CallReceived'
})

replace_in_file('app/src/main/java/com/example/ChatListScreen.kt', {
    'import androidx.compose.material.icons.Icons': 'import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.automirrored.outlined.Chat'
})

replace_in_file('app/src/main/java/com/example/MainActivity.kt', {
    'import androidx.compose.material.icons.Icons': 'import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.automirrored.filled.Chat\nimport androidx.compose.material.icons.automirrored.outlined.Chat'
})

