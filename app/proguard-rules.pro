# App Data Models for Firebase (Firestore / Realtime Database)
-keep class com.example.Story { *; }
-keep class com.example.Comment { *; }
-keep class com.example.ActiveCall { *; }
-keep class com.example.CallRecord { *; }
-keep class com.example.ChatMessage { *; }
-keep class com.example.ChatChannel { *; }
-keep class com.example.UserProfile { *; }
-keep class com.example.NearbyUser { *; }
-keep class com.example.PresenceState { *; }
-keep class com.example.Notification { *; }

# Firebase Firestore
-keepattributes Signature
-keepclassmembers class * {
  @com.google.firebase.firestore.PropertyName <fields>;
}

# Agora RTC
-keep class io.agora.** { *; }
-keep class io.agora.rtc2.** { *; }
-dontwarn io.agora.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Firebase Auth & Credential Manager
-keep class com.google.firebase.auth.** { *; }
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }

# Comprehensive Google Sign-In & Firebase Rules for Release Builds
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.firebase.** { *; }

# Prevent stripping of essential attributes for reflection/serialization
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
