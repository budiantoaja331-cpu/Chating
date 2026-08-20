sed -i '/import androidx.compose.ui.platform.LocalContext/d' app/src/main/java/com/example/MainActivity.kt
sed -i '/import androidx.lifecycle.viewmodel.compose.viewModel/d' app/src/main/java/com/example/MainActivity.kt
sed -i '/import androidx.compose.runtime.remember/d' app/src/main/java/com/example/MainActivity.kt

sed -i '1s/^/import androidx.compose.ui.platform.LocalContext\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.compose.runtime.remember\n/' app/src/main/java/com/example/MainActivity.kt
