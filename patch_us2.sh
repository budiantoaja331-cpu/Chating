sed -i 's/com.google.firebase.auth.try { FirebaseAuth.getInstance().signOut() } catch (e: Exception) {}/try { com.google.firebase.auth.FirebaseAuth.getInstance().signOut() } catch (e: Exception) {}/g' app/src/main/java/com/example/UserSessionManager.kt
sed -i 's/db?.collection("users").document/db?.collection("users")?.document/g' app/src/main/java/com/example/UserSessionManager.kt
