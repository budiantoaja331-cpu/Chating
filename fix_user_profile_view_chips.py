import re

path = 'app/src/main/java/com/example/UserProfileScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """                                            Text("Panggilan: ${profile.nickname.ifEmpty { "-" }}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Umur: ${if (profile.age > 0) "${profile.age} tahun" else "-"}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Minat: ${profile.interests.ifEmpty { "-" }}", style = MaterialTheme.typography.bodyMedium)"""

replacement = """                                            Text("Panggilan: ${profile.nickname.ifEmpty { "-" }}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Umur: ${if (profile.age > 0) "${profile.age} tahun" else "-"}", style = MaterialTheme.typography.bodyMedium)
                                            
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text("Minat:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            if (profile.interests.isNotEmpty()) {
                                                FlowRow(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    profile.interests.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { interest ->
                                                        AssistChip(
                                                            onClick = { },
                                                            label = { Text(interest) }
                                                        )
                                                    }
                                                }
                                            } else {
                                                Text("-", style = MaterialTheme.typography.bodyMedium)
                                            }"""

content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)

print("Updated view chips")
