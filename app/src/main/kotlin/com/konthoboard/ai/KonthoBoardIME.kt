package com.konthoboard.ai

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import android.widget.TextView
import com.konthoboard.ai.bangla.BanglaEngine
import com.konthoboard.ai.keyboard.*
import com.konthoboard.ai.prediction.PredictionEngine
import com.konthoboard.ai.theme.ThemeManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager

/**
 * KonthoBoardIME — Core Android IME Service.
 * Handles all keyboard input, mode switching, suggestions,
 * voice typing, and theme application.
 */
class KonthoBoardIME : InputMethodService(), KeyboardView.OnKeyListener {

    // ── UI ──────────────────────────────────────────────────────
    private lateinit var keyboardView: KeyboardView
    private lateinit var suggView1: TextView
    private lateinit var suggView2: TextView
    private lateinit var suggView3: TextView
    private lateinit var rootView: LinearLayout

    // ── State ────────────────────────────────────────────────────
    private var currentMode   = KeyboardConstants.MODE_ENGLISH
    private var isShifted     = false
    private var isCapsLock    = false
    private var showBanglaExtra = false
    private var composing     = StringBuilder()

    // ── Engines ──────────────────────────────────────────────────
    private val banglaEngine     = BanglaEngine()
    private val predictionEngine = PredictionEngine()

    // ── Voice ────────────────────────────────────────────────────
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    // ──────────────────────────────────────────────────────────────
    override fun onCreateInputView(): View {
        ThemeManager.init(applicationContext)
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)

        rootView   = view as LinearLayout
        keyboardView = KeyboardView(this)
        keyboardView.onKeyListener = this

        suggView1 = view.findViewById(R.id.suggestion_1)
        suggView2 = view.findViewById(R.id.suggestion_2)
        suggView3 = view.findViewById(R.id.suggestion_3)

        suggView1.setOnClickListener { commitSuggestion(suggView1.text.toString()) }
        suggView2.setOnClickListener { commitSuggestion(suggView2.text.toString()) }
        suggView3.setOnClickListener { commitSuggestion(suggView3.text.toString()) }

        val keysContainer = view.findViewById<LinearLayout>(R.id.keys_container)
        keysContainer.addView(keyboardView)

        view.findViewById<View>(R.id.tb_voice)?.setOnClickListener { startVoiceInput() }
        view.findViewById<View>(R.id.tb_settings)?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        loadKeyboard()
        return view
    }

    override fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        composing.clear()
        updateSuggestions("")
    }

    // ── Key Event Handler ────────────────────────────────────────
    override fun onKey(code: Int, label: String) {
        val ic: InputConnection = currentInputConnection ?: return

        when (code) {
            KeyboardConstants.KEY_DELETE -> {
                if (composing.isNotEmpty()) {
                    composing.deleteCharAt(composing.length - 1)
                    ic.setComposingText(composing, 1)
                    updateSuggestions(composing.toString())
                } else {
                    ic.deleteSurroundingText(1, 0)
                }
            }

            KeyboardConstants.KEY_ENTER -> {
                commitComposing()
                val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
                if (action == EditorInfo.IME_ACTION_SEARCH ||
                    action == EditorInfo.IME_ACTION_SEND ||
                    action == EditorInfo.IME_ACTION_GO) {
                    ic.performEditorAction(action)
                } else {
                    ic.commitText("\n", 1)
                }
            }

            KeyboardConstants.KEY_SPACE -> {
                if (composing.isNotEmpty()) {
                    commitComposing()
                } else {
                    ic.commitText(" ", 1)
                }
                updateSuggestions("")
            }

            KeyboardConstants.KEY_SHIFT -> {
                if (isCapsLock && isShifted) {
                    isCapsLock = false; isShifted = false
                } else if (isShifted) {
                    isCapsLock = true
                } else {
                    isShifted = true
                }
                loadKeyboard()
            }

            KeyboardConstants.KEY_MODE_EN -> {
                currentMode = KeyboardConstants.MODE_ENGLISH
                isShifted   = false
                composing.clear()
                loadKeyboard()
            }

            KeyboardConstants.KEY_MODE_BN -> {
                currentMode = KeyboardConstants.MODE_BANGLA
                composing.clear()
                loadKeyboard()
            }

            KeyboardConstants.KEY_NUMBERS -> {
                currentMode = KeyboardConstants.MODE_NUMBER
                loadKeyboard()
            }

            KeyboardConstants.KEY_SYMBOLS -> {
                currentMode = KeyboardConstants.MODE_SYMBOL
                loadKeyboard()
            }

            KeyboardConstants.KEY_VOICE -> startVoiceInput()

            else -> {
                val ch = label
                if (currentMode == KeyboardConstants.MODE_BANGLA) {
                    // Feed into Bangla engine
                    composing.append(ch)
                    val converted = banglaEngine.convert(composing.toString())
                    ic.setComposingText(converted, 1)
                    updateSuggestions(converted)
                } else {
                    // English / Number / Symbol
                    commitComposing()
                    val out = if (isShifted || isCapsLock) ch.uppercase() else ch
                    ic.commitText(out, 1)
                    if (isShifted && !isCapsLock) {
                        isShifted = false
                        loadKeyboard()
                    }
                    updateSuggestions(out)
                }
            }
        }
    }

    override fun onLongPress(code: Int, label: String) {
        // Long press delete → delete word
        if (code == KeyboardConstants.KEY_DELETE) {
            currentInputConnection?.deleteSurroundingText(5, 0)
        }
        // Long press space → switch mode
        if (code == KeyboardConstants.KEY_SPACE) {
            currentMode = if (currentMode == KeyboardConstants.MODE_ENGLISH)
                KeyboardConstants.MODE_BANGLA else KeyboardConstants.MODE_ENGLISH
            loadKeyboard()
        }
    }

    // ── Keyboard Loading ─────────────────────────────────────────
    private fun loadKeyboard() {
        val rows = when (currentMode) {
            KeyboardConstants.MODE_ENGLISH ->
                if (isShifted || isCapsLock) KeyboardLayout.ENGLISH_UPPER_ROWS
                else KeyboardLayout.ENGLISH_ROWS
            KeyboardConstants.MODE_BANGLA  -> KeyboardLayout.BANGLA_ROWS
            KeyboardConstants.MODE_NUMBER  -> KeyboardLayout.NUMBER_ROWS
            KeyboardConstants.MODE_SYMBOL  -> KeyboardLayout.SYMBOL_ROWS
            else -> KeyboardLayout.ENGLISH_ROWS
        }
        keyboardView.setRows(rows)
    }

    // ── Composing ────────────────────────────────────────────────
    private fun commitComposing() {
        val ic = currentInputConnection ?: return
        if (composing.isNotEmpty()) {
            val result = if (currentMode == KeyboardConstants.MODE_BANGLA)
                banglaEngine.convert(composing.toString())
            else composing.toString()
            ic.commitText(result, 1)
            ic.finishComposingText()
            composing.clear()
        }
    }

    private fun commitSuggestion(word: String) {
        val ic = currentInputConnection ?: return
        ic.finishComposingText()
        ic.deleteSurroundingText(composing.length, 0)
        ic.commitText("$word ", 1)
        composing.clear()
        updateSuggestions("")
    }

    // ── Suggestions ──────────────────────────────────────────────
    private fun updateSuggestions(input: String) {
        val suggs = predictionEngine.getSuggestions(input, currentMode)
        suggView1.text = suggs.getOrNull(0) ?: ""
        suggView2.text = suggs.getOrNull(1) ?: ""
        suggView3.text = suggs.getOrNull(2) ?: ""
    }

    // ── Voice Input ──────────────────────────────────────────────
    private fun startVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) return
        if (isListening) { speechRecognizer?.stopListening(); return }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                text?.firstOrNull()?.let {
                    currentInputConnection?.commitText("$it ", 1)
                }
                isListening = false
            }
            override fun onError(error: Int) { isListening = false }
            override fun onReadyForSpeech(params: Bundle?) { isListening = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                if (currentMode == KeyboardConstants.MODE_BANGLA) "bn-BD" else "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechRecognizer?.startListening(intent)
    }

    override fun onFinishInput() {
        super.onFinishInput()
        commitComposing()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
