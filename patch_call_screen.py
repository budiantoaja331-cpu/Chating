import re

path = 'app/src/main/java/com/example/CallScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add Icon imports if missing (FaceRetouchingNatural)
if "import androidx.compose.material.icons.filled.FaceRetouchingNatural" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Warning", "import androidx.compose.material.icons.filled.Warning\nimport androidx.compose.material.icons.filled.FaceRetouchingNatural")

# Target Row
target_buttons = """                // Tombol Putar Kamera (Switch Camera)
                if (isVideoCall) {
                    IconButton(
                        onClick = { viewModel.switchCamera() },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.DarkGray, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FlipCameraAndroid,
                            contentDescription = "Ganti Kamera",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(56.dp))
                }"""

replacement_buttons = """                // Tombol Putar Kamera (Switch Camera)
                if (isVideoCall) {
                    IconButton(
                        onClick = { viewModel.switchCamera() },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.DarkGray, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FlipCameraAndroid,
                            contentDescription = "Ganti Kamera",
                            tint = Color.White
                        )
                    }
                    
                    // Tombol Filter Wajah (Beauty Effect)
                    IconButton(
                        onClick = { viewModel.toggleBeautyEffect() },
                        modifier = Modifier
                            .size(56.dp)
                            .background(if (callState.isBeautyEffectEnabled) Color(0xFFFF4081) else Color.DarkGray, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FaceRetouchingNatural,
                            contentDescription = "Filter Cantik",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(56.dp))
                }"""

content = content.replace(target_buttons, replacement_buttons)

with open(path, 'w') as f:
    f.write(content)
