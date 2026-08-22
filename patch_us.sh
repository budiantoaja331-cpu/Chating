sed -i 's/db.collection/db?.collection/g' app/src/main/java/com/example/UserSessionManager.kt
sed -i 's/FirebaseAuth.getInstance().signOut()/try { FirebaseAuth.getInstance().signOut() } catch (e: Exception) {}/g' app/src/main/java/com/example/UserSessionManager.kt
