package com.example

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

data class LanguageOption(
    val code: String,
    val name: String,
    val flag: String,
    val nativeName: String
)

object LanguageHelper {
    const val PREFS_NAME = "app_language_prefs"
    const val KEY_LANGUAGE = "selected_language"

    val supportedLanguages = listOf(
        LanguageOption("en", "English", "🇬🇧", "English"),
        LanguageOption("id", "Indonesian", "🇮🇩", "Bahasa Indonesia"),
        LanguageOption("zh", "Chinese", "🇨🇳", "简体中文"),
        LanguageOption("es", "Spanish", "🇪🇸", "Español"),
        LanguageOption("ar", "Arabic", "🇸🇦", "العربية"),
        LanguageOption("ja", "Japanese", "🇯🇵", "日本語"),
        LanguageOption("fr", "French", "🇫🇷", "Français"),
        LanguageOption("de", "German", "🇩🇪", "Deutsch"),
        LanguageOption("pt", "Portuguese", "🇧🇷", "Português"),
        LanguageOption("ru", "Russian", "🇷🇺", "Русский")
    )

    private val _currentLanguage = MutableStateFlow(getSavedLanguageCode(null))
    val currentLanguage: StateFlow<String> = _currentLanguage

    fun getSavedLanguageCode(context: Context?): String {
        if (context == null) return "en"
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, Locale.getDefault().language) ?: "en"
    }

    fun setLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
        _currentLanguage.value = languageCode

        applyLanguage(context, languageCode)

        if (context is Activity) {
            context.recreate()
        }
    }

    fun applyLanguage(context: Context, languageCode: String? = null): Context {
        val code = languageCode ?: getSavedLanguageCode(context)
        val locale = Locale(code)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            return context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
            return context
        }
    }
}
