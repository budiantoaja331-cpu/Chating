package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(viewModel: UserProfileViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    if (uiState is UserProfileUiState.Success) {
                        IconButton(onClick = {
                            if (isEditing) {
                                // Save changes
                                viewModel.updateProfile(editName, editBio)
                                isEditing = false
                            } else {
                                // Enter edit mode
                                val profile = (uiState as UserProfileUiState.Success).profile
                                editName = profile.name
                                editBio = profile.bio
                                isEditing = true
                            }
                        }) {
                            Icon(
                                imageVector = if (isEditing) Icons.Filled.Edit else Icons.Filled.Edit,
                                contentDescription = if (isEditing) "Save Profile" else "Edit Profile",
                                tint = if (isEditing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            when (uiState) {
                is UserProfileUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 64.dp))
                }
                is UserProfileUiState.Error -> {
                    val message = (uiState as UserProfileUiState.Error).message
                    Text(
                        text = "Error: $message",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(32.dp)
                    )
                }
                is UserProfileUiState.Success -> {
                    val profile = (uiState as UserProfileUiState.Success).profile
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar Placeholder
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Avatar",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        if (isEditing) {
                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = editBio,
                                onValueChange = { editBio = it },
                                label = { Text("Bio") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 5
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    viewModel.updateProfile(editName, editBio)
                                    isEditing = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Profile")
                            }
                        } else {
                            Text(
                                text = profile.name.ifEmpty { "No Name" },
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = profile.bio.ifEmpty { "No bio added yet." },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
