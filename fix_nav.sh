# Hapus salah satu duplikat import NavType jika ada lebih dari 1
sed -i '0,/import androidx.navigation.NavType/!s/import androidx.navigation.NavType//g' app/src/main/java/com/example/MainActivity.kt

# Perbaiki Navigasi dengan pemisah yang benar (|) untuk menghindari bentrok /
sed -i 's|// placeholder navigation for now, later will go to actual call screen|val channelId = "call_" + minOf(userId, otherId) + "_" + maxOf(userId, otherId)\n                    navController.navigate("callScreen/$channelId/$isVideo")|' app/src/main/java/com/example/MainActivity.kt
