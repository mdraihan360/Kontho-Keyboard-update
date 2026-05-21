package com.konthoboard.ai.theme

import android.content.Context
import android.content.SharedPreferences
import com.konthoboard.ai.keyboard.KeyboardConstants

data class ThemeData(
    val name: String,
    val kbBg: String,
    val keyBg: String,
    val keySpecial: String,
    val keyText: String,
    val keySpecialText: String,
    val keyBorder: String,
    val keyShadow: String,
    val accent: String,
    val suggBg: String,
    val suggText: String,
)

object ThemeManager {

    private lateinit var prefs: SharedPreferences

    val THEMES: Map<String, ThemeData> = mapOf(
        KeyboardConstants.THEME_DARK to ThemeData(
            name = "Dark",
            kbBg = "#141428", keyBg = "#1E1E38", keySpecial = "#0F0F22",
            keyText = "#E8E8FF", keySpecialText = "#9999CC",
            keyBorder = "#2A2A50", keyShadow = "#08080F",
            accent = "#4F9DFF", suggBg = "#141428", suggText = "#AABBEE"
        ),
        KeyboardConstants.THEME_LIGHT to ThemeData(
            name = "Light",
            kbBg = "#D2D9E8", keyBg = "#FFFFFF", keySpecial = "#B8C2D8",
            keyText = "#1A1A3A", keySpecialText = "#445577",
            keyBorder = "#C5CDE0", keyShadow = "#A8B2C8",
            accent = "#1A73E8", suggBg = "#F0F4FF", suggText = "#334488"
        ),
        KeyboardConstants.THEME_AMOLED to ThemeData(
            name = "AMOLED",
            kbBg = "#000000", keyBg = "#0A0A0A", keySpecial = "#050505",
            keyText = "#FFFFFF", keySpecialText = "#888888",
            keyBorder = "#1A1A1A", keyShadow = "#000000",
            accent = "#00E5FF", suggBg = "#000000", suggText = "#00BFFF"
        ),
        KeyboardConstants.THEME_RGB to ThemeData(
            name = "RGB Gaming",
            kbBg = "#0C0C2A", keyBg = "#131340", keySpecial = "#080820",
            keyText = "#FFFFFF", keySpecialText = "#FF88FF",
            keyBorder = "#FF00FF", keyShadow = "#000000",
            accent = "#FF00FF", suggBg = "#080820", suggText = "#00FFFF"
        ),
        KeyboardConstants.THEME_GLASS to ThemeData(
            name = "Glass",
            kbBg = "#1A1A2E", keyBg = "#FFFFFF1A", keySpecial = "#FFFFFF0A",
            keyText = "#FFFFFF", keySpecialText = "#AACCFF",
            keyBorder = "#FFFFFF26", keyShadow = "#0000004D",
            accent = "#88CCFF", suggBg = "#FFFFFF0D", suggText = "#AADDFF"
        )
    )

    val current: ThemeData
        get() {
            val key = if (::prefs.isInitialized)
                prefs.getString(KeyboardConstants.PREF_THEME, KeyboardConstants.THEME_DARK)
                    ?: KeyboardConstants.THEME_DARK
            else KeyboardConstants.THEME_DARK
            return THEMES[key] ?: THEMES[KeyboardConstants.THEME_DARK]!!
        }

    fun init(context: Context) {
        prefs = context.getSharedPreferences("konthoboard_prefs", Context.MODE_PRIVATE)
    }

    fun setTheme(context: Context, key: String) {
        prefs.edit().putString(KeyboardConstants.PREF_THEME, key).apply()
    }
}
