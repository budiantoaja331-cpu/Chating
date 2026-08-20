with open('app/src/main/java/com/example/MyApplication.kt', 'r') as f:
    content = f.read()

new_content = """package com.example

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
        } catch (e: Exception) {
            Log.e("AppStartup", "CRITICAL: FirebaseApp initialization failed!", e)
        }
    }
}"""

with open('app/src/main/java/com/example/MyApplication.kt', 'w') as f:
    f.write(new_content)
