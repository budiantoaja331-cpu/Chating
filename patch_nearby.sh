sed -i 's/fun NearbyScreen(viewModel: NearbyViewModel = viewModel()) {/fun NearbyScreen(\n    viewModel: NearbyViewModel = viewModel(),\n    onNavigateToChat: (String, String) -> Unit = { _, _ -> }\n) {/g' app/src/main/java/com/example/NearbyScreen.kt
sed -i 's/NearbyUserCard(user)/NearbyUserCard(user, onNavigateToChat)/g' app/src/main/java/com/example/NearbyScreen.kt
sed -i 's/fun NearbyUserCard(user: NearbyUser) {/fun NearbyUserCard(user: NearbyUser, onNavigateToChat: (String, String) -> Unit) {/g' app/src/main/java/com/example/NearbyScreen.kt
sed -i 's/onClick = { \/\* TODO: Aksi sapa \/ chat \*\/ }/onClick = { onNavigateToChat(user.id, user.name) }/g' app/src/main/java/com/example/NearbyScreen.kt
