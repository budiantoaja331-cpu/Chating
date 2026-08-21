package com.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    viewModel: UserProfileViewModel,
    onProfileComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var nickname by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }

    // Pre-fill if already exists
    LaunchedEffect(uiState) {
        if (uiState is UserProfileUiState.Success) {
            val profile = (uiState as UserProfileUiState.Success).profile
            if (nickname.isEmpty() && profile.nickname.isNotEmpty()) nickname = profile.nickname
            if (ageText.isEmpty() && profile.age > 0) ageText = profile.age.toString()
            if (interests.isEmpty() && profile.interests.isNotEmpty()) interests = profile.interests
        }
    }

    val predefinedInterests = listOf("conten", "hiburan", "cari patner fantasi", "d'patner", "cari pasangan seumur hidup", "sewa pacar", "penyedia pacar sewa")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lengkapi Profil Anda", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Selamat datang! Silakan lengkapi data diri Anda terlebih dahulu.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nama Panggilan") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = ageText,
                onValueChange = { ageText = it },
                label = { Text("Umur") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            Text(
                "Pilih Minat Anda:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            
            val selectedInterests = interests.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
            
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                predefinedInterests.forEach { interest ->
                    val isSelected = selectedInterests.contains(interest)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedInterests.remove(interest)
                            } else {
                                selectedInterests.add(interest)
                            }
                            interests = selectedInterests.joinToString(", ")
                        },
                        label = { Text(interest) }
                    )
                }
            }

            Button(
                onClick = {
                    val age = ageText.toIntOrNull() ?: 0
                    if (nickname.isNotBlank() && age > 0 && interests.isNotBlank()) {
                        // Assuming 'name' and 'bio' are already kept or default. 
                        // We extract current name and bio from the state if possible.
                        var currentName = "User"
                        var currentBio = "Ini adalah bio saya."
                        if (uiState is UserProfileUiState.Success) {
                            currentName = (uiState as UserProfileUiState.Success).profile.name
                            currentBio = (uiState as UserProfileUiState.Success).profile.bio
                        }
                        
                        viewModel.updateProfile(
                            name = currentName,
                            bio = currentBio,
                            nickname = nickname,
                            age = age,
                            interests = interests
                        )
                        onProfileComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = nickname.isNotBlank() && ageText.isNotBlank() && interests.isNotBlank()
            ) {
                Text("Simpan dan Lanjutkan")
            }
        }
    }
}
