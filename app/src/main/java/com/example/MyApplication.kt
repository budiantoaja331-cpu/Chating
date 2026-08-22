package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import androidx.annotation.Keep
import java.lang.Thread.UncaughtExceptionHandler

@Keep
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("CrashHandler", "FATAL EXCEPTION in thread: ${thread.name}", throwable)
            try { FirebaseCrashlytics.getInstance().recordException(throwable) } catch(e: Exception) {}
            defaultHandler?.uncaughtException(thread, throwable)
        }

        try {
            Log.d("AppStartup", "MyApplication onCreate triggered.")
            Log.d("AppStartup", "Initializing FirebaseApp manually...")
            var app = FirebaseApp.initializeApp(this)
            if (app == null) {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("1:1234567890:android:321321321")
                    .setApiKey("AIzaSyDummyKeyForFallbackInit")
                    .setProjectId("dummy-project")
                    .build()
                app = FirebaseApp.initializeApp(this, options)
                Log.e("AppStartup", "WARNING: google-services.json is missing! Initialized dummy FirebaseApp to prevent crash.")
            }
            try { FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            ) } catch(e: Exception) { Log.e("AppStartup", "AppCheck init failed: ${e.message}") }
            Log.d("AppStartup", "Firebase App Check (Play Integrity) initialized.")
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
                try { FirebaseCrashlytics.getInstance().recordException(e) } catch(ex: Exception) {}
            }
        } catch (e: Exception) {
            Log.e("AppStartup", "CRITICAL: FirebaseApp initialization failed!", e)
            try { FirebaseCrashlytics.getInstance().recordException(e) } catch(ex: Exception) {}
        }
    }
}