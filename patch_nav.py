import re

path = 'app/src/main/java/com/example/MainActivity.kt'
with open(path, 'r') as f:
    content = f.read()

# Add import android.net.Uri
if 'import android.net.Uri' not in content:
    content = content.replace('import android.os.Bundle', 'import android.os.Bundle\nimport android.net.Uri')

content = content.replace('navController.navigate("chatRoom/$otherUserId/$otherUserName")', 'navController.navigate("chatRoom/$otherUserId/${Uri.encode(otherUserName)}")')
content = content.replace('navController.navigate("chatRoom/$targetUserId/$targetUserName")', 'navController.navigate("chatRoom/$targetUserId/${Uri.encode(targetUserName)}")')

with open(path, 'w') as f:
    f.write(content)

print("Patched MainActivity navigation")
