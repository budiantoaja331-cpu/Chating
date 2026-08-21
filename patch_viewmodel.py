import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    fun blockUser(targetUserId: String) {
        viewModelScope.launch {
            try {
                db.collection("users").document(currentUserId).update(
                    "blockedUsers", FieldValue.arrayUnion(targetUserId)
                ).await()
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error blocking user", e)
            }
        }
    }"""

replacement = """    fun blockUser(targetUserId: String) {
        viewModelScope.launch {
            try {
                db.collection("users").document(currentUserId).update(
                    "blockedUsers", FieldValue.arrayUnion(targetUserId)
                ).await()
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error blocking user", e)
            }
        }
    }
    
    fun reportStory(storyId: String) {
        viewModelScope.launch {
            try {
                val reportData = hashMapOf(
                    "storyId" to storyId,
                    "reporterId" to currentUserId,
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("reports").add(reportData).await()
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error reporting story", e)
            }
        }
    }"""

content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)
print("Added reportStory to StoryViewModel")
