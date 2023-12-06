package com.cofbro.qian.friend.search

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.os.Handler
import android.os.Looper
import android.util.Log

class DelayedTextWatcher(private val editText: EditText, private val delayMillis: Long, private val action: () -> Unit,private val preAction:() -> Unit) : TextWatcher {
    private val handler = Handler(Looper.getMainLooper())
    private var lastInputTime: Long = 0
    private var search = false
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Not used
        if(!search){
            preAction.invoke()
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Not used
    }

    override fun afterTextChanged(editable: Editable?) {
        handler.removeCallbacksAndMessages(null) // Remove previous callbacks

        val input = editable?.toString()?.trim()
        if (input.isNullOrEmpty()) {
            return
        }

        val lastChar = input.last()
        if (Character.isDigit(lastChar)) {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastInput = currentTime - lastInputTime
            Log.v("TIME",timeSinceLastInput.toString())
            if (timeSinceLastInput >= delayMillis) {
                search = true
                action.invoke()
            } else {
                search=false
                val delay = delayMillis - timeSinceLastInput
                handler.postDelayed({ action.invoke() }, delay)
            }
            lastInputTime = currentTime
        }
    }
}


