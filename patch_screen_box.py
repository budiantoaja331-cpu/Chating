import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {"""
replacement = """                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .clickable {
                                            if (isEditing) {
                                                cropImage.launch(
                                                    CropImageContractOptions(
                                                        uri = null,
                                                        cropImageOptions = CropImageOptions(
                                                            imageSourceIncludeGallery = true,
                                                            imageSourceIncludeCamera = true,
                                                            cropShape = CropImageView.CropShape.OVAL,
                                                            fixAspectRatio = true,
                                                            aspectRatioX = 1,
                                                            aspectRatioY = 1
                                                        )
                                                    )
                                                )
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {"""
content = content.replace(target, replacement)

# Add a loading indicator on top of avatar if uploading
target2 = """                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }"""
replacement2 = """                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    if (isUploadingAvatar) {
                                        Box(
                                            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = Color.White)
                                        }
                                    } else if (isEditing) {
                                        Box(
                                            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit Avatar",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }"""
content = content.replace(target2, replacement2)

# Ensure Color is imported
if "import androidx.compose.ui.graphics.Color" not in content:
    content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.ui.graphics.Color")

with open(path, 'w') as f:
    f.write(content)
