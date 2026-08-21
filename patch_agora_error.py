import re

path = 'app/src/main/java/com/example/CallViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("CallViewModel", "User Offline: $uid")
            _callState.update { it.copy(remoteUid = null) }
        }
    }"""

replacement = """        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("CallViewModel", "User Offline: $uid")
            _callState.update { it.copy(remoteUid = null) }
        }
        
        override fun onError(err: Int) {
            Log.e("CallViewModel", "Agora Error: $err")
            // Optional: you can expose this error to the UI state
        }
    }"""

content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)
