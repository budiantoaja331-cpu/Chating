package com.example

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID
import kotlin.random.Random

class UserManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getUserId(): String {
        var id = prefs.getString("user_id", null)
        if (id == null) {
            id = UUID.randomUUID().toString()
            prefs.edit().putString("user_id", id).apply()
        }
        return id
    }

    fun getUserName(): String {
        var name = prefs.getString("user_name", null)
        if (name == null) {
            val randomNumber = Random.nextInt(1000, 9999)
            name = "Guest_$randomNumber"
            prefs.edit().putString("user_name", name).apply()
        }
        return name
    }
}
