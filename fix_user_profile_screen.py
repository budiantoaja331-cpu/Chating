import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add edit state variables
target_state = """    var editName by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }"""
replacement_state = """    var editName by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }
    var editNickname by remember { mutableStateOf("") }
    var editAge by remember { mutableStateOf("") }
    var editInterests by remember { mutableStateOf("") }"""

content = content.replace(target_state, replacement_state)

# Add saving logic to the TopAppBar action
target_save = """                                // Save changes
                                viewModel.updateProfile(editName, editBio)
                                isEditing = false"""
replacement_save = """                                // Save changes
                                viewModel.updateProfile(editName, editBio, editNickname, editAge.toIntOrNull() ?: 0, editInterests)
                                isEditing = false"""
content = content.replace(target_save, replacement_save)

# Enter edit mode
target_edit_mode = """                                // Enter edit mode
                                val profile = (uiState as UserProfileUiState.Success).profile
                                editName = profile.name
                                editBio = profile.bio
                                isEditing = true"""
replacement_edit_mode = """                                // Enter edit mode
                                val profile = (uiState as UserProfileUiState.Success).profile
                                editName = profile.name
                                editBio = profile.bio
                                editNickname = profile.nickname
                                editAge = if (profile.age > 0) profile.age.toString() else ""
                                editInterests = profile.interests
                                isEditing = true"""
content = content.replace(target_edit_mode, replacement_edit_mode)

# Save Button inside Column
target_btn = """                                    Button(
                                        onClick = {
                                            viewModel.updateProfile(editName, editBio)
                                            isEditing = false
                                        },"""
replacement_btn = """                                    Button(
                                        onClick = {
                                            viewModel.updateProfile(editName, editBio, editNickname, editAge.toIntOrNull() ?: 0, editInterests)
                                            isEditing = false
                                        },"""
content = content.replace(target_btn, replacement_btn)

# Add UI fields for editing
target_edit_ui = """                                    OutlinedTextField(
                                        value = editBio,
                                        onValueChange = { editBio = it },
                                        label = { Text("Bio") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        maxLines = 5
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))"""
replacement_edit_ui = """                                    OutlinedTextField(
                                        value = editBio,
                                        onValueChange = { editBio = it },
                                        label = { Text("Bio") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        maxLines = 5
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = editNickname,
                                        onValueChange = { editNickname = it },
                                        label = { Text("Nama Panggilan") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = editAge,
                                        onValueChange = { editAge = it },
                                        label = { Text("Umur") },
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = editInterests,
                                        onValueChange = { editInterests = it },
                                        label = { Text("Minat / Hobi") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))"""
content = content.replace(target_edit_ui, replacement_edit_ui)

# Add UI fields for displaying
target_display_ui = """                                    Text(
                                        text = profile.bio.ifEmpty { "No bio added yet." },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )"""
replacement_display_ui = """                                    Text(
                                        text = profile.bio.ifEmpty { "No bio added yet." },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                            Text("Data Diri", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Panggilan: ${profile.nickname.ifEmpty { "-" }}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Umur: ${if (profile.age > 0) "${profile.age} tahun" else "-"}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Minat: ${profile.interests.ifEmpty { "-" }}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }"""
content = content.replace(target_display_ui, replacement_display_ui)

with open(path, 'w') as f:
    f.write(content)
print("Updated UserProfileScreen")
