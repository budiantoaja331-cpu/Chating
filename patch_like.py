import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                // Update Firestore
                viewModelScope.launch {
                    try {
                        val isLiking = newLikes.contains(currentUserId)
                        storiesCollection.document(storyId).update(
                            "likedByUsers", if (isLiking) FieldValue.arrayUnion(currentUserId) else FieldValue.arrayRemove(currentUserId),
                            "likesCount", if (isLiking) FieldValue.increment(1) else FieldValue.increment(-1)
                        ).await()
                    } catch (e: Exception) {
                        Log.e("StoryViewModel", "Error toggling like", e)
                    }
                }"""

replacement = """                // Update Firestore
                viewModelScope.launch {
                    try {
                        val isLiking = newLikes.contains(currentUserId)
                        val storyRef = storiesCollection.document(storyId)
                        val likeRef = storyRef.collection("likes").document(currentUserId)
                        
                        if (isLiking) {
                            val likeData = hashMapOf(
                                "userId" to currentUserId,
                                "timestamp" to System.currentTimeMillis()
                            )
                            likeRef.set(likeData).await()
                            storyRef.update(
                                "likedByUsers", FieldValue.arrayUnion(currentUserId),
                                "likesCount", FieldValue.increment(1)
                            ).await()
                        } else {
                            likeRef.delete().await()
                            storyRef.update(
                                "likedByUsers", FieldValue.arrayRemove(currentUserId),
                                "likesCount", FieldValue.increment(-1)
                            ).await()
                        }
                    } catch (e: Exception) {
                        Log.e("StoryViewModel", "Error toggling like", e)
                    }
                }"""

if target in content:
    content = content.replace(target, replacement)
else:
    print("TARGET NOT FOUND!")

with open(path, 'w') as f:
    f.write(content)
