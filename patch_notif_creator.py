import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

# For toggleLike
target_like = """                            storyRef.update(
                                "likedByUsers", FieldValue.arrayUnion(currentUserId),
                                "likesCount", FieldValue.increment(1)
                            ).await()"""

replacement_like = """                            storyRef.update(
                                "likedByUsers", FieldValue.arrayUnion(currentUserId),
                                "likesCount", FieldValue.increment(1)
                            ).await()
                            
                            if (story.authorId != currentUserId) {
                                val notif = Notification(
                                    targetUserId = story.authorId,
                                    sourceUserId = currentUserId,
                                    sourceUserName = currentUserName,
                                    type = "like",
                                    storyId = storyId
                                )
                                db.collection("notifications").document(notif.id).set(notif)
                            }"""

if target_like in content:
    content = content.replace(target_like, replacement_like)
else:
    print("TARGET LIKE NOT FOUND")


# For addComment
target_comment = """                // Increment commentsCount in the story document
                storiesCollection.document(storyId).update("commentsCount", FieldValue.increment(1)).await()"""

replacement_comment = """                // Increment commentsCount in the story document
                storiesCollection.document(storyId).update("commentsCount", FieldValue.increment(1)).await()
                
                // Get the story author to send notification
                val storyDoc = storiesCollection.document(storyId).get().await()
                val storyAuthorId = storyDoc.getString("authorId")
                if (storyAuthorId != null && storyAuthorId != currentUserId) {
                    val notif = Notification(
                        targetUserId = storyAuthorId,
                        sourceUserId = currentUserId,
                        sourceUserName = currentUserName,
                        type = "comment",
                        storyId = storyId,
                        content = content
                    )
                    db.collection("notifications").document(notif.id).set(notif)
                }"""

if target_comment in content:
    content = content.replace(target_comment, replacement_comment)
else:
    print("TARGET COMMENT NOT FOUND")

with open(path, 'w') as f:
    f.write(content)
