package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import androidx.annotation.Keep
import java.lang.Thread.UncaughtExceptionHandler

@Keep
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("CrashHandler", "FATAL EXCEPTION in thread: ${thread.name}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        try {
            Log.d("AppStartup", "MyApplication onCreate triggered.")
            Log.d("AppStartup", "Initializing FirebaseApp manually...")
            FirebaseApp.initializeApp(this)
            Log.d("AppStartup", "FirebaseApp initialized successfully.")

            // Configure Firestore Persistent Cache / Offline Persistence
            try {
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .setCacheSizeBytes(com.google.firebase.firestore.FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
                firestore.firestoreSettings = settings
                Log.d("AppStartup", "Firestore offline persistence enabled successfully.")
            } catch (e: Exception) {
                Log.e("AppStartup", "Note on Firestore settings: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e("AppStartup", "CRITICAL: FirebaseApp initialization failed!", e)
        }
    }
}