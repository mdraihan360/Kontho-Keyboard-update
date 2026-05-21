override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
            touchDownTime = SystemClock.uptimeMillis()
            findKey(event.x, event.y)?.let { (rect, key) ->
                pressedKey  = key
                pressedRect = rect
                invalidate()
                scheduleLongPress(key)
            }
        }

        MotionEvent.ACTION_UP -> {
            stopLongPressAction()

            val elapsed = SystemClock.uptimeMillis() - touchDownTime

            if (elapsed < LONG_PRESS_MS) {
                pressedKey?.let {
                    onKeyListener?.onKey(it.code, it.label)
                }
                vibrate(30)
            }

            pressedKey  = null
            pressedRect = null
            invalidate()
        }

        MotionEvent.ACTION_CANCEL -> {
            stopLongPressAction()

            pressedKey  = null
            pressedRect = null
            invalidate()
        }
    }

    return true
}

private fun findKey(x: Float, y: Float) =
    keyRects.firstOrNull { (rect, _) -> rect.contains(x, y) }

private fun scheduleLongPress(key: KeyDef) {
    longPressRunnable = Runnable {
        onKeyListener?.onLongPress(key.code, key.label)
        vibrate(60)
    }

    postDelayed(longPressRunnable, LONG_PRESS_MS)
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
    return (dp * resources.displayMetrics.density).toInt()
}
