import re

path = 'app/src/main/java/com/example/ChatRoomViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace(
    'import com.google.firebase.firestore.Query',
    'import com.google.firebase.firestore.Query\nimport com.google.firebase.firestore.SetOptions'
)

target = """                // Update channel last message
                chatsCollection.document(channelId).update(
                    mapOf(
                        "lastMessage" to message.text,
                        "lastMessageTime" to message.timestamp
                    )
                ).await()"""

replacement = """                // Update channel last message using Set with merge to avoid not-found errors
                chatsCollection.document(channelId).set(
                    mapOf(
                        "lastMessage" to message.text,
                        "lastMessageTime" to message.timestamp
                    ), SetOptions.merge()
                ).await()"""

content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)
print("Updated ChatRoomViewModel")
