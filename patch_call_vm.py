import re

path = 'app/src/main/java/com/example/CallViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

# Add import
import_statement = "import io.agora.rtc2.video.BeautyOptions\n"
content = content.replace("import io.agora.rtc2.video.VideoCanvas\n", "import io.agora.rtc2.video.VideoCanvas\n" + import_statement)

# Update CallState
target_state = """    val isMuted: Boolean = false,
    val isVideoEnabled: Boolean = true,
    val connectionStatus: String = "Menghubungkan..."
)"""
replacement_state = """    val isMuted: Boolean = false,
    val isVideoEnabled: Boolean = true,
    val isBeautyEffectEnabled: Boolean = false,
    val connectionStatus: String = "Menghubungkan..."
)"""
content = content.replace(target_state, replacement_state)

# Add toggleBeautyEffect
target_methods = """    fun switchCamera() {
        rtcEngine?.switchCamera()
    }"""
replacement_methods = """    fun switchCamera() {
        rtcEngine?.switchCamera()
    }

    fun toggleBeautyEffect() {
        val enabled = !_callState.value.isBeautyEffectEnabled
        val options = BeautyOptions().apply {
            lighteningContrastLevel = BeautyOptions.LIGHTENING_CONTRAST_NORMAL
            lighteningLevel = 0.7f
            smoothnessLevel = 0.6f
            rednessLevel = 0.1f
            sharpnessLevel = 0.3f
        }
        rtcEngine?.setBeautyEffectOptions(enabled, options)
        _callState.update { it.copy(isBeautyEffectEnabled = enabled) }
    }"""
content = content.replace(target_methods, replacement_methods)

with open(path, 'w') as f:
    f.write(content)
