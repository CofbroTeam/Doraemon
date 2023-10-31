package com.cofbro.qian.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import com.cofbro.qian.R

class GestureInputDialog(context: Context) : AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_gesture_input)
        val layoutParams = window?.attributes
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        layoutParams?.width = context.resources.displayMetrics.widthPixels - 200
        window?.attributes = layoutParams
    }

    fun setInputEndListener(inputEndListener: (List<Int>) -> Unit) {
        findViewById<GestureInputView>(R.id.gestureInputView).setInputEndListener(inputEndListener)
    }

    override fun show() {
        super.show()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    }
}