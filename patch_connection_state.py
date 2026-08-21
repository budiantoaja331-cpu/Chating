import re

path = 'app/src/main/java/com/example/CallViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target_data_class = """data class CallState(
    val isJoined: Boolean = false,
    val remoteUid: Int? = null,
    val isMuted: Boolean = false,
    val isVideoEnabled: Boolean = true
)"""
replacement_data_class = """data class CallState(
    val isJoined: Boolean = false,
    val remoteUid: Int? = null,
    val isMuted: Boolean = false,
    val isVideoEnabled: Boolean = true,
    val connectionStatus: String = "Menghubungkan..."
)"""
content = content.replace(target_data_class, replacement_data_class)

target_event_handler = """        override fun onError(err: Int) {
            Log.e("CallViewModel", "Agora Error: $err")
            // Optional: you can expose this error to the UI state
        }
    }"""
replacement_event_handler = """        override fun onError(err: Int) {
            Log.e("CallViewModel", "Agora Error: $err")
            // Optional: you can expose this error to the UI state
        }
        
        override fun onConnectionStateChanged(state: Int, reason: Int) {
            val statusStr = when(state) {
                Constants.CONNECTION_STATE_CONNECTING -> "Menghubungkan..."
                Constants.CONNECTION_STATE_CONNECTED -> "Terhubung"
                Constants.CONNECTION_STATE_RECONNECTING -> "Menyambung kembali (Sinyal Lemah)..."
                Constants.CONNECTION_STATE_FAILED -> "Koneksi Gagal"
                Constants.CONNECTION_STATE_DISCONNECTED -> "Terputus"
                else -> "Menunggu..."
            }
            _callState.update { it.copy(connectionStatus = statusStr) }
        }
    }"""
content = content.replace(target_event_handler, replacement_event_handler)

with open(path, 'w') as f:
    f.write(content)
