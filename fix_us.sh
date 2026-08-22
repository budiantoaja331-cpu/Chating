sed -i 's/document(userId).addSnapshotListener/document(userId)?.addSnapshotListener/g' app/src/main/java/com/example/UserSessionManager.kt
sed -i 's/document(uid).update/document(uid)?.update/g' app/src/main/java/com/example/UserSessionManager.kt
