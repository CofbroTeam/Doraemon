package com.cofbro.qian.friend.search

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.os.Handler
import android.os.Looper

class DelayedTextWatcher(
    private val editText: EditText,
    private val delayMillis: Long,
    private val action: () -> Unit,
    private val onTextCleared: () -> Unit
) : TextWatcher {
    private val handler = Handler(Looper.getMainLooper())
    private var lastInputTime: Long = 0
    private var search = false
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(editable: Editable?) {
        if (editText.text.isEmpty()) {
            onTextCleared.invoke()
        }
        handler.removeCallbacksAndMessages(null) // Remove previous callbacks
        val input = editable?.toString()?.trim()
        if (input.isNullOrEmpty() || lastInputTime == 0L) {
            lastInputTime = System.currentTimeMillis()
            return
        }
        //val lastChar = input.last()
        val currentTime = System.currentTimeMillis()
        val timeSinceLastInput = currentTime - lastInputTime
        if (timeSinceLastInput >= delayMillis) {
            search = true
            action.invoke()
        } else {
            search = false
            val delay = delayMillis - timeSinceLastInput
            handler.postDelayed({ action.invoke() }, delay)
        }
        lastInputTime = currentTime
    }
}


