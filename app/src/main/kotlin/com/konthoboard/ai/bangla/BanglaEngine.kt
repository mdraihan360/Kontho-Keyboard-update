package com.konthoboard.ai.bangla

/**
 * BanglaEngine — Avro Phonetic + Conjunct generation.
 * Converts Roman input string → Bangla Unicode output.
 */
class BanglaEngine {

    /**
     * Convert Roman phonetic string to Bangla Unicode.
     * Supports Avro phonetic rules + conjuncts.
     */
    fun convert(input: String): String {
        if (input.isEmpty()) return ""

        // If input is already Bangla (direct key press), return as-is
        if (input.all { it > '\u0900' }) return input

        var result = input

        // ── Step 1: Multi-char replacements (order matters — longest first) ──
        AVRO_MAP.entries.sortedByDescending { it.key.length }.forEach { (roman, bangla) ->
            result = result.replace(roman, bangla)
        }

        // ── Step 2: Conjunct formation via hasanta ──
        result = formConjuncts(result)

        return result
    }

    /**
     * Form conjuncts using hasanta (্) between consonants.
     * e.g. ক + ্ + ষ → ক্ষ
     */
    private fun formConjuncts(input: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < input.length) {
            val ch = input[i]
            sb.append(ch)

            // If current char is a consonant followed by hasanta + consonant
            if (isBanglaConsonant(ch) && i + 2 < input.length &&
                input[i + 1] == '\u09CD' && isBanglaConsonant(input[i + 2])) {
                sb.append('\u09CD')  // hasanta
                sb.append(input[i + 2])
                i += 3
            } else {
                i++
            }
        }
        return sb.toString()
    }

    private fun isBanglaConsonant(ch: Char) = ch in '\u0995'..'\u09B9' ||
            ch == '\u09CE' || ch == '\u09DC' || ch == '\u09DD' || ch == '\u09DF'

    companion object {
        // Avro phonetic map (Roman → Bangla Unicode)
        // Ordered by key length descending for greedy matching
        val AVRO_MAP: Map<String, String> = linkedMapOf(
            // 3-char
            "ksh" to "ক্ষ", "jNG" to "জ্ঞ", "NgG" to "ঙ্গ",
            // 2-char consonants
            "kh"  to "খ",  "gh"  to "ঘ",  "ng"  to "ঙ",
            "ch"  to "চ",  "chh" to "ছ",  "jh"  to "ঝ",
            "Th"  to "ঠ",  "Dh"  to "ঢ",
            "th"  to "থ",  "dh"  to "ধ",
            "ph"  to "ফ",  "bh"  to "ভ",
            "sh"  to "শ",  "Sh"  to "ষ",
            "rr"  to "ড়", "Rh"  to "ঢ়",
            "ng"  to "ঙ",  "NG"  to "ঞ",
            // Vowels
            "aa"  to "আ",  "ii"  to "ঈ",  "uu"  to "ঊ",
            "oi"  to "ঐ",  "ou"  to "ঔ",  "rri" to "ঋ",
            // Single consonants
            "k"   to "ক",  "g"   to "গ",  "c"   to "চ",
            "j"   to "জ",  "T"   to "ট",  "D"   to "ড",
            "N"   to "ণ",  "t"   to "ত",  "d"   to "দ",
            "n"   to "ন",  "p"   to "প",  "f"   to "ফ",
            "b"   to "ব",  "m"   to "ম",  "z"   to "য",
            "r"   to "র",  "l"   to "ল",  "S"   to "ষ",
            "s"   to "স",  "h"   to "হ",  "R"   to "ড়",
            "y"   to "য়", "q"   to "ক",  "w"   to "ও",
            "x"   to "ক্স",
            // Single vowels
            "a"   to "আ",  "i"   to "ই",  "u"   to "উ",
            "e"   to "এ",  "o"   to "ও",
            // Matras
            "aa"  to "া",  "ii"  to "ী",  "uu"  to "ূ",
            // Special chars
            ":"   to "ঃ",  "^"   to "ঁ",  "ng"  to "ং",
            "."   to "।"
        )

        // All 30 standard conjuncts
        val CONJUNCTS: Map<String, String> = mapOf(
            "ক্ষ" to "ksh",  "জ্ঞ" to "jNG",  "ত্র" to "tr",
            "শ্র" to "shr",  "হ্ম" to "hm",   "ক্ত" to "kt",
            "গ্ধ" to "gdh",  "চ্ছ" to "cch",  "দ্ব" to "db",
            "গ্ন" to "gn",   "ষ্ণ" to "ShN",  "ক্স" to "ks",
            "প্ল" to "pl",   "ব্ল" to "bl",   "গ্ল" to "gl",
            "ন্ত" to "nt",   "ন্দ" to "nd",   "ম্ব" to "mb",
            "ষ্ট" to "ShT",  "ষ্ঠ" to "ShTh", "ষ্প" to "Shp",
            "স্ত" to "st",   "স্থ" to "sth",  "স্ন" to "sn",
            "স্প" to "sp",   "স্ব" to "sv",   "স্ম" to "sm",
            "ক্ক" to "kk",   "ত্ত" to "tt",   "ন্ন" to "nn"
        )
    }
}
