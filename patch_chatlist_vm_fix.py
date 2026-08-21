import re

path = 'app/src/main/java/com/example/ChatListViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                    // Filter out blocked users
                    viewModelScope.launch {
                        UserSessionManager.blockedUsers.collect { blockedUsers ->
                            val filteredChannels = channels.filter { channel ->
                                val otherUserId = channel.participants.firstOrNull { it != currentUserId } ?: ""
                                otherUserId !in blockedUsers
                            }
                            _uiState.value = ChatListUiState.Success(filteredChannels)
                        }
                    }"""
replacement = """                    _rawChannels = channels
                    updateUiState()"""

content = content.replace(target, replacement)

# Add raw channels and observe UserSessionManager
if "private var _rawChannels" not in content:
    target_init = """    init {
        listenForChats()
    }"""
    replacement_init = """    private var _rawChannels = listOf<ChatChannel>()

    init {
        listenForChats()
        viewModelScope.launch {
            UserSessionManager.blockedUsers.collect {
                updateUiState()
            }
        }
    }

    private fun updateUiState() {
        val blockedUsers = UserSessionManager.blockedUsers.value
        val filteredChannels = _rawChannels.filter { channel ->
            val otherUserId = channel.participants.firstOrNull { it != currentUserId } ?: ""
            otherUserId !in blockedUsers
        }
        _uiState.value = ChatListUiState.Success(filteredChannels)
    }"""
    content = content.replace(target_init, replacement_init)

with open(path, 'w') as f:
    f.write(content)
