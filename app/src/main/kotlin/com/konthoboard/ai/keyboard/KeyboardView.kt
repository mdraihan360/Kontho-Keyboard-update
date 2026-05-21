package com.konthoboard.ai.keyboard

import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.konthoboard.ai.theme.ThemeData
import com.konthoboard.ai.theme.ThemeManager

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    interface OnKeyListener {
        fun onKey(code: Int, label: String)
        fun onLongPress(code: Int, label: String)
    }

    var onKeyListener: OnKeyListener? = null

    private var rows: List<List<KeyDef>> = emptyList()

    private var keyHeight =
        dpToPx(KeyboardConstants.KEY_HEIGHT_NORMAL)

    private var keyGap =
        dpToPx(KeyboardConstants.KEY_GAP)

    private val keyRects =
        mutableListOf<Pair<RectF, KeyDef>>()

    private var pressedKey: KeyDef? = null
    private var pressedRect: RectF? = null

    private val keyPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val shadowPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val borderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val cornerRadius =
        dpToPx(8).toFloat()

    private val vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE)
                as? Vibrator

    private var longPressRunnable: Runnable? = null

    private var touchDownTime = 0L

    private val LONG_PRESS_MS = 400L

    init {
        textPaint.typeface = Typeface.DEFAULT
        textPaint.textAlign = Paint.Align.CENTER

        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth =
            dpToPx(1).toFloat()
    }

    fun setRows(
        rows: List<List<KeyDef>>,
        keyHeightDp: Int =
            KeyboardConstants.KEY_HEIGHT_NORMAL
    ) {
        this.rows = rows
        this.keyHeight = dpToPx(keyHeightDp)

        requestLayout()
        invalidate()
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        val w = MeasureSpec.getSize(widthMeasureSpec)

        val h =
            rows.size * (keyHeight + keyGap) + keyGap

        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        buildKeyRects(w)
    }

    private fun buildKeyRects(totalWidth: Int) {

        keyRects.clear()

        var y = keyGap.toFloat()

        for (row in rows) {

            val totalWeight =
                row.sumOf {
                    it.widthWeight.toDouble()
                }.toFloat()

            val availWidth =
                totalWidth -
                        keyGap * (row.size + 1)

            var x = keyGap.toFloat()

            for (key in row) {

                val kw =
                    availWidth *
                            key.widthWeight /
                            totalWeight

                val rect =
                    RectF(
                        x,
                        y,
                        x + kw,
                        y + keyHeight
                    )

                keyRects.add(
                    Pair(rect, key)
                )

                x += kw + keyGap
            }

            y += keyHeight + keyGap
        }
    }

    override fun onDraw(canvas: Canvas) {

        val theme = ThemeManager.current

        canvas.drawColor(
            Color.parseColor(theme.kbBg)
        )

        for ((rect, key) in keyRects) {

            val isPressed =
                key == pressedKey

            val isSpecial =
                key.isSpecial

            shadowPaint.color =
                Color.parseColor(theme.keyShadow)

            canvas.drawRoundRect(
                RectF(
                    rect.left,
                    rect.top + dpToPx(3),
                    rect.right,
                    rect.bottom + dpToPx(3)
                ),
                cornerRadius,
                cornerRadius,
                shadowPaint
            )

            keyPaint.color =
                when {

                    isPressed ->
                        Color.parseColor(theme.accent)

                    isSpecial ->
                        Color.parseColor(theme.keySpecial)

                    else ->
                        Color.parseColor(theme.keyBg)
                }

            canvas.drawRoundRect(
                rect,
                cornerRadius,
                cornerRadius,
                keyPaint
            )

            borderPaint.color =
                Color.parseColor(theme.keyBorder)

            canvas.drawRoundRect(
                rect,
                cornerRadius,
                cornerRadius,
                borderPaint
            )

            val label = key.label

            val cx = rect.centerX()
            val cy = rect.centerY()

            when {

                label == "⌫" ||
                        label == "↵" -> {

                    drawIcon(
                        canvas,
                        label,
                        cx,
                        cy,
                        isPressed,
                        theme
                    )
                }

                label.length > 1 -> {

                    textPaint.textSize =
                        dpToPx(11).toFloat()

                    textPaint.color =
                        if (isPressed)
                            Color.WHITE
                        else
                            Color.parseColor(
                                theme.keySpecialText
                            )

                    canvas.drawText(
                        label,
                        cx,
                        cy +
                                textPaint.textSize * 0.35f,
                        textPaint
                    )
                }

                else -> {

                    textPaint.textSize =
                        dpToPx(18).toFloat()

                    textPaint.color =
                        if (isPressed)
                            Color.WHITE
                        else
                            Color.parseColor(
                                theme.keyText
                            )

                    canvas.drawText(
                        label,
                        cx,
                        cy +
                                textPaint.textSize * 0.35f,
                        textPaint
                    )
                }
            }
        }
    }

    private fun drawIcon(
        canvas: Canvas,
        icon: String,
        cx: Float,
        cy: Float,
        pressed: Boolean,
        theme: ThemeData
    ) {

        textPaint.textSize =
            dpToPx(16).toFloat()

        textPaint.color =
            if (pressed)
                Color.WHITE
            else
                Color.parseColor(
                    theme.keySpecialText
                )

        canvas.drawText(
            icon,
            cx,
            cy + textPaint.textSize * 0.35f,
            textPaint
        )
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {

                touchDownTime =
                    SystemClock.uptimeMillis()

                findKey(
                    event.x,
                    event.y
                )?.let { (rect, key) ->

                    pressedKey = key
                    pressedRect = rect

                    invalidate()

                    scheduleLongPress(key)
                }
            }

            MotionEvent.ACTION_UP -> {

                stopLongPressAction()

                val elapsed =
                    SystemClock.uptimeMillis() -
                            touchDownTime

                if (elapsed < LONG_PRESS_MS) {

                    pressedKey?.let {

                        onKeyListener?.onKey(
                            it.code,
                            it.label
                        )
                    }

                    vibrate(30)
                }

                pressedKey = null
                pressedRect = null

                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {

                stopLongPressAction()

                pressedKey = null
                pressedRect = null

                invalidate()
            }
        }

        return true
    }

    private fun findKey(
        x: Float,
        y: Float
    ) =
        keyRects.firstOrNull {
            (rect, _) ->
            rect.contains(x, y)
        }

    private fun scheduleLongPress(
        key: KeyDef
    ) {

        longPressRunnable = Runnable {

            onKeyListener?.onLongPress(
                key.code,
                key.label
            )

            vibrate(60)
        }

        postDelayed(
            longPressRunnable,
            LONG_PRESS_MS
        )
    }

    private fun stopLongPressAction() {

        longPressRunnable?.let {
            removeCallbacks(it)
        }

        longPressRunnable = null
    }

    private fun vibrate(ms: Long) {

        vibrator?.vibrate(
            VibrationEffect.createOneShot(
                ms,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }

    private fun dpToPx(dp: Int): Int {

        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }
}
