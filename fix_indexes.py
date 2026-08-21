import re

# Fix ChatListViewModel
path = 'app/src/main/java/com/example/ChatListViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace(
    '.orderBy("lastMessageTime", Query.Direction.DESCENDING)',
    '// .orderBy removed to prevent Firestore composite index requirements'
)
content = content.replace(
    '_uiState.value = ChatListUiState.Success(channels)',
    'channels.sortByDescending { it.lastMessageTime }\n                    _uiState.value = ChatListUiState.Success(channels)'
)

with open(path, 'w') as f:
    f.write(content)


# Fix CallHistoryViewModel
path2 = 'app/src/main/java/com/example/CallHistoryViewModel.kt'
with open(path2, 'r') as f:
    content2 = f.read()

content2 = content2.replace(
    '.orderBy("timestamp", Query.Direction.DESCENDING)',
    '// .orderBy removed to prevent Firestore composite index requirements'
)
content2 = content2.replace(
    'val calls = snapshot.documents.mapNotNull { it.toObject(CallRecord::class.java) }',
    'val calls = snapshot.documents.mapNotNull { it.toObject(CallRecord::class.java) }.sortedByDescending { it.timestamp }'
)

with open(path2, 'w') as f:
    f.write(content2)

print("Fixed Firestore Index Errors")
