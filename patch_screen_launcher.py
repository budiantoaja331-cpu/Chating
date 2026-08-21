import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    var selectedTab by remember { mutableStateOf(0) } // 0 = My Posts, 1 = Saved"""
replacement = """    var selectedTab by remember { mutableStateOf(0) } // 0 = My Posts, 1 = Saved
    var isUploadingAvatar by remember { mutableStateOf(false) }

    val cropImage = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            if (uriContent != null) {
                isUploadingAvatar = true
                viewModel.updateAvatar(uriContent) { success ->
                    isUploadingAvatar = false
                }
            }
        }
    }"""
content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)
