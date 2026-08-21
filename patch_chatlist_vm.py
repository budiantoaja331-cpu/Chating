import re

path = 'app/src/main/java/com/example/ChatListViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                if (snapshot != null) {
                    val channels = mutableListOf<ChatChannel>()
                    for (doc in snapshot.documents) {
                        val channel = doc.toObject(ChatChannel::class.java)
                        if (channel != null) {
                            channels.add(channel)
                        }
                    }
                    channels.sortByDescending { it.lastMessageTime }
                    _uiState.value = ChatListUiState.Success(channels)
                }"""
replacement = """                if (snapshot != null) {
                    val channels = mutableListOf<ChatChannel>()
                    for (doc in snapshot.documents) {
                        val channel = doc.toObject(ChatChannel::class.java)
                        if (channel != null) {
                            channels.add(channel)
                        }
                    }
                    channels.sortByDescending { it.lastMessageTime }
                    
                    // Filter out blocked users
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        UserSessionManager.blockedUsers.collect { blockedUsers ->
                            val filteredChannels = channels.filter { channel ->
                                val otherUserId = channel.participants.firstOrNull { it != currentUserId } ?: ""
                                otherUserId !in blockedUsers
                            }
                            _uiState.value = ChatListUiState.Success(filteredChannels)
                        }
                    }
                }"""

if "import kotlinx.coroutines.launch" not in content:
    content = content.replace("import kotlinx.coroutines.flow.asStateFlow", "import kotlinx.coroutines.flow.asStateFlow\nimport kotlinx.coroutines.launch\nimport androidx.lifecycle.viewModelScope\nimport com.example.UserSessionManager")

target_full = """                    // Filter out blocked users
                    viewModelScope.launch {
                        UserSessionManager.blockedUsers.collect { blockedUsers ->
                            val filteredChannels = channels.filter { channel ->
                                val otherUserId = channel.participants.firstOrNull { it != currentUserId } ?: ""
                                otherUserId !in blockedUsers
                            }
                            _uiState.value = ChatListUiState.Success(filteredChannels)
                        }
                    }"""

content = content.replace(target, replacement)
content = content.replace("kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch", "viewModelScope.launch")
with open(path, 'w') as f:
    f.write(content)
