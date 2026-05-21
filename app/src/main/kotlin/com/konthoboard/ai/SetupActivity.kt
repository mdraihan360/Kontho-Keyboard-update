package com.konthoboard.ai

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SetupActivity : AppCompatActivity() {

    private lateinit var btnEnable: Button
    private lateinit var btnDefault: Button
    private lateinit var btnSettings: Button
    private lateinit var tvStep1Done: TextView
    private lateinit var tvStep2Done: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        btnEnable   = findViewById(R.id.btn_enable)
        btnDefault  = findViewById(R.id.btn_default)
        btnSettings = findViewById(R.id.btn_settings)
        tvStep1Done = findViewById(R.id.tv_step1_done)
        tvStep2Done = findViewById(R.id.tv_step2_done)

        btnEnable.setOnClickListener {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            startActivity(intent)
        }

        btnDefault.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        checkStatus()
    }

    private fun checkStatus() {
        // Check if IME is enabled
        val enabledMethods = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_INPUT_METHODS
        ) ?: ""
        val isEnabled = enabledMethods.contains(packageName)

        if (isEnabled) {
            tvStep1Done.visibility = android.view.View.VISIBLE
            btnEnable.text = "✅ কীবোর্ড সক্রিয় আছে"
        }

        // Check if it is default
        val defaultMethod = Settings.Secure.getString(
            contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD
        ) ?: ""
        val isDefault = defaultMethod.contains(packageName)

        if (isDefault) {
            tvStep2Done.visibility = android.view.View.VISIBLE
            btnDefault.text = "✅ ডিফল্ট সেট আছে"
        }
    }
}
