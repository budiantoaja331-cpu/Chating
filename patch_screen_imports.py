import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """import coil.compose.AsyncImage"""
replacement = """import coil.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import androidx.compose.foundation.clickable"""
content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)
