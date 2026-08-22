sed -i 's/FirebaseAppCheck.getInstance().installAppCheckProviderFactory(/try { FirebaseAppCheck.getInstance().installAppCheckProviderFactory(/g' app/src/main/java/com/example/MyApplication.kt
sed -i 's/PlayIntegrityAppCheckProviderFactory.getInstance()/PlayIntegrityAppCheckProviderFactory.getInstance()/g' app/src/main/java/com/example/MyApplication.kt
sed -i 's/            )/            ) } catch(e: Exception) { Log.e("AppStartup", "AppCheck init failed: ${e.message}") }/g' app/src/main/java/com/example/MyApplication.kt
