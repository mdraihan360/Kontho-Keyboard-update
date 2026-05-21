package com.konthoboard.ai.prediction

import com.konthoboard.ai.keyboard.KeyboardConstants

/**
 * PredictionEngine — AI next-word suggestions.
 * Returns ranked word suggestions for the current input + mode.
 */
class PredictionEngine {

    private val banglaDict = listOf(
        "আমি","তুমি","সে","আমরা","তোমরা","আপনি","আমাকে","তোমাকে","আপনাকে",
        "ভালো","ভালোবাসা","ভালোবাসি","ভালোবাসা","ভালো লাগে",
        "আছি","আছো","আছেন","কেমন","কেমন আছো","কেমন আছেন",
        "ধন্যবাদ","আপনাকে ধন্যবাদ","অনেক ধন্যবাদ",
        "হ্যালো","শুভ সকাল","শুভ রাত্রি","শুভ বিকাল",
        "ইনশাআল্লাহ","আলহামদুলিল্লাহ","মাশাআল্লাহ","সুবহানআল্লাহ",
        "দয়া করে","অনুগ্রহ করে","আবার","সময়মতো",
        "হ্যাঁ","না","অবশ্যই","নিশ্চয়ই","সত্যিই",
        "আজকে","গতকাল","আগামীকাল","এখন","পরে",
        "বাংলাদেশ","ঢাকা","চট্টগ্রাম","সিলেট","রাজশাহী",
        "বাড়ি","স্কুল","কলেজ","বিশ্ববিদ্যালয়","অফিস",
        "খাওয়া","ঘুমানো","পড়াশোনা","কাজ করা","বেড়ানো"
    )

    private val englishDict = listOf(
        "Hello","Hi","Hey","Good morning","Good evening","Good night",
        "How are","How are you","I am","I'm","You are","We are",
        "Thank you","Thanks","Please","Sorry","Excuse me","You're welcome",
        "What","When","Where","Why","How","Who",
        "Can you","Could you","Would you","I will","I can","Let me",
        "That's","This is","There is","It is","They are",
        "Good","Great","Amazing","Wonderful","Awesome","Nice",
        "I love","I like","I hate","I think","I feel","I know",
        "Yes","No","Maybe","Of course","Absolutely","Sure",
        "Today","Tomorrow","Yesterday","Now","Later","Soon",
        "Bangladesh","Dhaka","Chittagong","Sylhet","Rajshahi"
    )

    /**
     * Returns top 3 suggestions for given input and keyboard mode.
     */
    fun getSuggestions(input: String, mode: Int): List<String> {
        if (input.isEmpty()) {
            return when (mode) {
                KeyboardConstants.MODE_BANGLA -> listOf("আমি","আপনি","ভালো")
                else -> listOf("Hello","I am","Thank")
            }
        }

        val dict = if (mode == KeyboardConstants.MODE_BANGLA) banglaDict else englishDict
        val lower = input.lowercase()

        // Priority 1: starts with input
        val starts = dict.filter {
            it.lowercase().startsWith(lower)
        }

        // Priority 2: contains input
        val contains = dict.filter {
            it.lowercase().contains(lower) && !starts.contains(it)
        }

        return (starts + contains).take(3).ifEmpty {
            if (mode == KeyboardConstants.MODE_BANGLA)
                listOf("আমি","তুমি","সে")
            else
                listOf("Hello","Thank","Please")
        }
    }
}
