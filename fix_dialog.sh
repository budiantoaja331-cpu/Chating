# Remove the bad block
sed -i '/val callManager = remember(userId) { ActiveCallManager(userId) }/,/        )/d' app/src/main/java/com/example/MainActivity.kt
sed -i '/    incomingCall?.let { call ->/,/    }/d' app/src/main/java/com/example/MainActivity.kt

# Insert it back after val navController
sed -i '/val context = LocalContext.current/a \    val callManager = remember(userId) { ActiveCallManager(userId) }\n    val incomingCall by callManager.incomingCall.collectAsState()\n\n    DisposableEffect(userId) {\n        callManager.startListening()\n        onDispose { callManager.stopListening() }\n    }\n\n    incomingCall?.let { call ->\n        IncomingCallDialog(\n            call = call,\n            onAccept = {\n                callManager.acceptCall(call.id)\n                navController.navigate("callScreen/${call.channelId}/${call.isVideoCall}")\n            },\n            onReject = {\n                callManager.rejectCall(call.id)\n            }\n        )\n    }' app/src/main/java/com/example/MainActivity.kt
