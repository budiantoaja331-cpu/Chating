import re

path = 'app/src/main/java/com/example/MainActivity.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    DisposableEffect(userId) {
        callManager.startListening()
        onDispose { callManager.stopListening() }
    }"""
replacement = """    DisposableEffect(userId) {
        callManager.startListening()
        PresenceManagerInstance.instance.startTracking(userId)
        onDispose { 
            callManager.stopListening() 
            PresenceManagerInstance.instance.stopTracking()
        }
    }"""

content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)
