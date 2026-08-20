sed -i '/import androidx.navigation.NavType/d' app/src/main/java/com/example/MainActivity.kt
sed -i '/import androidx.navigation.navArgument/d' app/src/main/java/com/example/MainActivity.kt
sed -i '1s/^/import androidx.navigation.NavType\nimport androidx.navigation.navArgument\n/' app/src/main/java/com/example/MainActivity.kt
