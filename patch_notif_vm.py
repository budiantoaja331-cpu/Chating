import re

path = 'app/src/main/java/com/example/NotificationViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """import com.example.auth.UserSessionManager"""
content = content.replace(target, "import com.google.firebase.auth.FirebaseAuth")

target2 = """    val currentUserId = UserSessionManager.currentUserId.value"""
replacement2 = """    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid"""
content = content.replace(target2, replacement2)

with open(path, 'w') as f:
    f.write(content)
