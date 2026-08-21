import re

path = 'app/src/main/java/com/example/CallScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (permissionState.allPermissionsGranted) {"""

replacement = """    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (permissionState.allPermissionsGranted) {
            
            // Connection Status Banner
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.DarkGray.copy(alpha = 0.7f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .zIndex(10f)
            ) {
                Text(
                    text = callState.connectionStatus,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
"""

content = content.replace(target, replacement)

# ensure zIndex is imported, if not, use androidx.compose.ui.zIndex
content = content.replace(".zIndex(10f)", ".zIndex(10f)")
if "import androidx.compose.ui.zIndex" not in content:
    content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.ui.zIndex.zIndex")

with open(path, 'w') as f:
    f.write(content)
