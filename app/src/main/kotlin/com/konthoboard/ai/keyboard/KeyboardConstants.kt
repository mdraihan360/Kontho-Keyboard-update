package com.konthoboard.ai.keyboard

object KeyboardConstants {

    // Key codes
    const val KEY_DELETE   = -5
    const val KEY_ENTER    = 10
    const val KEY_SPACE    = 32
    const val KEY_SHIFT    = -1
    const val KEY_MODE_EN  = -2
    const val KEY_MODE_BN  = -3
    const val KEY_NUMBERS  = -4
    const val KEY_SYMBOLS  = -6
    const val KEY_VOICE    = -7
    const val KEY_EMOJI    = -8
    const val KEY_SETTINGS = -9

    // Keyboard modes
    const val MODE_ENGLISH  = 0
    const val MODE_BANGLA   = 1
    const val MODE_NUMBER   = 2
    const val MODE_SYMBOL   = 3

    // Theme keys
    const val THEME_DARK   = "dark"
    const val THEME_LIGHT  = "light"
    const val THEME_AMOLED = "amoled"
    const val THEME_RGB    = "rgb"
    const val THEME_GLASS  = "glass"

    // Pref keys
    const val PREF_THEME       = "pref_theme"
    const val PREF_VIBRATION   = "pref_vibration"
    const val PREF_SOUND       = "pref_sound"
    const val PREF_AUTOCORRECT = "pref_autocorrect"
    const val PREF_SUGGESTION  = "pref_suggestion"
    const val PREF_KB_HEIGHT   = "pref_height"
    const val PREF_BANGLA_MODE = "pref_bangla_mode"

    // Bangla modes
    const val BANGLA_PHONETIC = "phonetic"
    const val BANGLA_AVRO     = "avro"
    const val BANGLA_BIJOY    = "bijoy"

    // Key dimensions (dp)
    const val KEY_HEIGHT_NORMAL  = 48
    const val KEY_HEIGHT_TALL    = 56
    const val KEY_HEIGHT_COMPACT = 40
    const val KEY_GAP            = 4
}
