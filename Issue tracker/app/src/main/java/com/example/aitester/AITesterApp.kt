package com.example.aitester

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

class AITesterApp : Application() {

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LANGUAGE = "language"

        const val DARK_MODE_SYSTEM = "system"
        const val DARK_MODE_LIGHT = "light"
        const val DARK_MODE_DARK = "dark"

        const val LANG_SYSTEM = "system"
        const val LANG_EN = "en"
        const val LANG_CS = "cs"
        const val LANG_SK = "sk"
        const val LANG_DE = "de"
        const val LANG_DE_AT = "de_AT"
        const val LANG_PL = "pl"
        const val LANG_IT = "it"
        const val LANG_RU = "ru"
        const val LANG_UK = "uk"
    }

    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        applyDarkMode()
    }

    override fun attachBaseContext(base: Context) {
        prefs = base.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lang = prefs.getString(KEY_LANGUAGE, LANG_EN) ?: LANG_EN
        val context = applyLanguageToContext(base, lang)
        super.attachBaseContext(context)
    }

    private fun applyDarkMode() {
        val mode = prefs.getString(KEY_DARK_MODE, DARK_MODE_SYSTEM) ?: DARK_MODE_SYSTEM
        val nightMode = when (mode) {
            DARK_MODE_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            DARK_MODE_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun applyLanguageToContext(context: Context, language: String): Context {
        @Suppress("DEPRECATION")
        val locale = when (language) {
            LANG_EN -> Locale("en")
            LANG_CS -> Locale("cs")
            LANG_SK -> Locale("sk")
            LANG_DE -> Locale("de")
            LANG_DE_AT -> Locale("de", "AT")
            LANG_PL -> Locale("pl")
            LANG_IT -> Locale("it")
            LANG_RU -> Locale("ru")
            LANG_UK -> Locale("uk")
            else -> Locale("en")
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
}
