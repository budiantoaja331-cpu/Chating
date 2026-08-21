import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Update signature
target_sig = """@Composable
fun StoryScreen(viewModel: StoryViewModel = viewModel()) {"""
replacement_sig = """@Composable
fun StoryScreen(
    viewModel: StoryViewModel = viewModel(),
    onNavigateToCreatePost: () -> Unit = {}
) {"""
content = content.replace(target_sig, replacement_sig)

# Remove showAddDialog state
content = re.sub(r'    var showAddDialog by remember { mutableStateOf\(false\) }\n', '', content)

# Change FAB onClick
content = content.replace('onClick = { showAddDialog = true }', 'onClick = onNavigateToCreatePost')

# Remove Add Story dialog
dialog_regex = r'    if \(showAddDialog\).*?        }\n    }'
content = re.sub(dialog_regex, '', content, flags=re.DOTALL)

with open(path, 'w') as f:
    f.write(content)
print("Updated StoryScreen")
