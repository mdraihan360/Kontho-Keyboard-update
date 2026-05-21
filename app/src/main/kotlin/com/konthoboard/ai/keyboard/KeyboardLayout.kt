package com.konthoboard.ai.keyboard

/**
 * Defines all keyboard row/key data.
 * Each KeyDef holds: label, code, width weight, isSpecial flag.
 */
data class KeyDef(
    val label: String,
    val code: Int,
    val widthWeight: Float = 1f,
    val isSpecial: Boolean = false,
    val popupKeys: List<String> = emptyList()
)

object KeyboardLayout {

    // ── ENGLISH QWERTY ──────────────────────────────────────────
    val ENGLISH_ROWS: List<List<KeyDef>> = listOf(
        listOf("q","w","e","r","t","y","u","i","o","p").map { c ->
            KeyDef(c, c[0].code, popupKeys = getEnglishPopup(c))
        },
        listOf("a","s","d","f","g","h","j","k","l").map { c ->
            KeyDef(c, c[0].code, popupKeys = getEnglishPopup(c))
        },
        listOf(
            KeyDef("⇧", KeyboardConstants.KEY_SHIFT, 1.5f, true),
            *listOf("z","x","c","v","b","n","m").map { c ->
                KeyDef(c, c[0].code)
            }.toTypedArray(),
            KeyDef("⌫", KeyboardConstants.KEY_DELETE, 1.5f, true)
        ),
        listOf(
            KeyDef("?123", KeyboardConstants.KEY_NUMBERS, 1.5f, true),
            KeyDef(",", ','.code, 1f, popupKeys = listOf("!","?")),
            KeyDef("SPACE", KeyboardConstants.KEY_SPACE, 5f, true),
            KeyDef(".", '.'.code, 1f, popupKeys = listOf("…","·")),
            KeyDef("↵", KeyboardConstants.KEY_ENTER, 1.5f, true)
        )
    )

    val ENGLISH_UPPER_ROWS: List<List<KeyDef>> =
        ENGLISH_ROWS.map { row ->
            row.map { key ->
                if (key.code in 'a'.code..'z'.code)
                    key.copy(label = key.label.uppercase(), code = key.label.uppercase()[0].code)
                else key
            }
        }

    // ── BANGLA PHONETIC ─────────────────────────────────────────
    val BANGLA_ROWS: List<List<KeyDef>> = listOf(
        listOf(
            KeyDef("ক", 'ক'.code), KeyDef("খ", 'খ'.code), KeyDef("গ", 'গ'.code),
            KeyDef("ঘ", 'ঘ'.code), KeyDef("ঙ", 'ঙ'.code), KeyDef("চ", 'চ'.code),
            KeyDef("ছ", 'ছ'.code), KeyDef("জ", 'জ'.code), KeyDef("ঝ", 'ঝ'.code),
            KeyDef("ঞ", 'ঞ'.code)
        ),
        listOf(
            KeyDef("ট", 'ট'.code), KeyDef("ঠ", 'ঠ'.code), KeyDef("ড", 'ড'.code),
            KeyDef("ঢ", 'ঢ'.code), KeyDef("ণ", 'ণ'.code), KeyDef("ত", 'ত'.code),
            KeyDef("থ", 'থ'.code), KeyDef("দ", 'দ'.code), KeyDef("ধ", 'ধ'.code)
        ),
        listOf(
            KeyDef("⇧", KeyboardConstants.KEY_SHIFT, 1.5f, true),
            KeyDef("ন", 'ন'.code), KeyDef("প", 'প'.code), KeyDef("ফ", 'ফ'.code),
            KeyDef("ব", 'ব'.code), KeyDef("ভ", 'ভ'.code), KeyDef("ম", 'ম'.code),
            KeyDef("য", 'য'.code),
            KeyDef("⌫", KeyboardConstants.KEY_DELETE, 1.5f, true)
        ),
        listOf(
            KeyDef("EN", KeyboardConstants.KEY_MODE_EN, 1.5f, true),
            KeyDef("র", 'র'.code), KeyDef("ল", 'ল'.code),
            KeyDef("SPACE", KeyboardConstants.KEY_SPACE, 5f, true),
            KeyDef("শ", 'শ'.code), KeyDef("হ", 'হ'.code),
            KeyDef("↵", KeyboardConstants.KEY_ENTER, 1.5f, true)
        )
    )

    // Extra Bangla row (vowels + matras + special chars)
    val BANGLA_EXTRA_ROW: List<KeyDef> = listOf(
        KeyDef("অ",'অ'.code), KeyDef("আ",'আ'.code), KeyDef("ই",'ই'.code),
        KeyDef("ঈ",'ঈ'.code), KeyDef("উ",'উ'.code), KeyDef("ঊ",'ঊ'.code),
        KeyDef("এ",'এ'.code), KeyDef("ও",'ও'.code), KeyDef("ঐ",'ঐ'.code),
        KeyDef("ঔ",'ঔ'.code), KeyDef("া",'\u09BE'.code), KeyDef("ি",'\u09BF'.code),
        KeyDef("ী",'\u09C0'.code), KeyDef("ু",'\u09C1'.code), KeyDef("ূ",'\u09C2'.code),
        KeyDef("ে",'\u09CB'.code.let{'\u09C7'.code}), KeyDef("ো",'\u09CB'.code),
        KeyDef("্",'\u09CD'.code), KeyDef("ৃ",'\u09C3'.code),
        KeyDef("ং",'ং'.code), KeyDef("ঃ",'ঃ'.code), KeyDef("ঁ",'ঁ'.code),
        KeyDef("ৎ",'ৎ'.code), KeyDef("ড়",'ড়'.code), KeyDef("ঢ়",'ঢ়'.code),
        KeyDef("য়",'য়'.code), KeyDef("।", '।'.code), KeyDef("॥",'॥'.code)
    )

    // ── NUMBER ROW ──────────────────────────────────────────────
    val NUMBER_ROWS: List<List<KeyDef>> = listOf(
        listOf("1","2","3","4","5","6","7","8","9","0").mapIndexed { i, c ->
            KeyDef(c, '0'.code + if (i==9) 0 else i+1,
                popupKeys = getNumberPopup(c))
        },
        listOf("-","/",":",";","(",")","%","@","\"","'").map { c ->
            KeyDef(c, c[0].code)
        },
        listOf(
            KeyDef("#+=", KeyboardConstants.KEY_SYMBOLS, 1.5f, true),
            *listOf(".","?","!","'",",").map { c -> KeyDef(c, c[0].code) }.toTypedArray(),
            KeyDef("⌫", KeyboardConstants.KEY_DELETE, 1.5f, true)
        ),
        listOf(
            KeyDef("ABC", KeyboardConstants.KEY_MODE_EN, 1.5f, true),
            KeyDef("_", '_'.code),
            KeyDef("SPACE", KeyboardConstants.KEY_SPACE, 5f, true),
            KeyDef("↵", KeyboardConstants.KEY_ENTER, 1.5f, true)
        )
    )

    // ── SYMBOL ROW ──────────────────────────────────────────────
    val SYMBOL_ROWS: List<List<KeyDef>> = listOf(
        listOf("[","]","{","}","#","%","^","*","+","=").map { KeyDef(it, it[0].code) },
        listOf("_","\\","|","~","<",">","€","£","¥","•").map { KeyDef(it, it[0].code) },
        listOf(
            KeyDef("123", KeyboardConstants.KEY_NUMBERS, 1.5f, true),
            *listOf(".",",","?","!","'").map { KeyDef(it, it[0].code) }.toTypedArray(),
            KeyDef("⌫", KeyboardConstants.KEY_DELETE, 1.5f, true)
        ),
        listOf(
            KeyDef("ABC", KeyboardConstants.KEY_MODE_EN, 1.5f, true),
            KeyDef("SPACE", KeyboardConstants.KEY_SPACE, 5f, true),
            KeyDef("↵", KeyboardConstants.KEY_ENTER, 1.5f, true)
        )
    )

    private fun getEnglishPopup(k: String): List<String> = when(k) {
        "e" -> listOf("è","é","ê","ë")
        "a" -> listOf("à","á","â","ä","å","æ")
        "i" -> listOf("ì","í","î","ï")
        "o" -> listOf("ò","ó","ô","ö","ø")
        "u" -> listOf("ù","ú","û","ü")
        "n" -> listOf("ñ")
        "c" -> listOf("ç")
        "s" -> listOf("ß","$")
        "1" -> listOf("¹","½","⅓")
        else -> emptyList()
    }

    private fun getNumberPopup(k: String): List<String> = when(k) {
        "1" -> listOf("¹","½")
        "2" -> listOf("²","⅔")
        "3" -> listOf("³","⅓")
        "0" -> listOf("°","∅")
        else -> emptyList()
    }
}
