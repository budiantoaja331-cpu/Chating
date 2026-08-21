import re

path = 'app/src/main/java/com/example/ProfileSetupScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add ExperimentalLayoutApi opt-in
content = content.replace(
    '@OptIn(ExperimentalMaterial3Api::class)',
    '@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)'
)

# Define predefined interests
predefined_interests = '    val predefinedInterests = listOf("conten", "hiburan", "cari patner fantasi", "d\'patner", "cari pasangan seumur hidup", "sewa pacar", "penyedia pacar sewa")'

# Replace the interests text field
target_textfield = """            OutlinedTextField(
                value = interests,
                onValueChange = { interests = it },
                label = { Text("Minat / Hobi (contoh: Musik, Olahraga)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            )"""

replacement_chips = """            Text(
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
            }"""

# Insert predefined interests just before Scaffold
target_scaffold = "    Scaffold("
content = content.replace(target_scaffold, predefined_interests + "\n\n" + target_scaffold)

content = content.replace(target_textfield, replacement_chips)

with open(path, 'w') as f:
    f.write(content)

print("Updated ProfileSetupScreen")
