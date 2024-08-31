package com.orion.voxy_test

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TypingTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var currentText: String = ""
    private var handler = Handler()
    private var isAnimating = false
    private var cursorVisible = true

    private val cursorBlinkRunnable = object : Runnable {
        override fun run() {
            cursorVisible = !cursorVisible
            text = currentText + if (cursorVisible) "|" else ""
            handler.postDelayed(this, 500)
        }
    }

    fun appendPartialText(partialText: String) {
        if (partialText != currentText) {
            currentText = partialText
            text = partialText + if (cursorVisible) "|" else ""
        }
    }

    fun finalizeText(finalText: String) {
        handler.removeCallbacksAndMessages(null)
        currentText = finalText
        text = finalText
        //startCursorBlink()
    }

    fun resetText() {
        handler.removeCallbacksAndMessages(null)
        currentText = ""
        text = ""
        cursorVisible = true
        isAnimating = false
    }

    private fun startCursorBlink() {
        handler.post(cursorBlinkRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacksAndMessages(null)
    }
}
