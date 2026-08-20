# Firebase Firestore
-keepattributes Signature
-keepclassmembers class * {
  @com.google.firebase.firestore.PropertyName <fields>;
}
-keep class com.example.Story { *; }
-keep class com.example.ActiveCall { *; }
-keep class com.example.CallRecord { *; }
-keep class com.example.ChatMessage { *; }
-keep class com.example.User { *; }
-keep class com.example.** { *; }

# Agora
-keep class io.agora.** { *; }
-keep class io.agora.rtc2.** { *; }
-dontwarn io.agora.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Firebase Auth & Credential Manager
-keep class com.google.firebase.auth.** { *; }
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }
-keep class com.example.MyApplication { *; }
