package com.cofbro.qian.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.cofbro.qian.R

class WaitDialog(context: Context) : AlertDialog(context) {
    private var positiveButton: TextView? = null
    private var negativeButton: TextView? = null
    private var positiveClickListener: ((View) -> Unit)? = null
    private var negativeClickListener: ((View) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_wait)
        val layoutParams = window?.attributes
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        layoutParams?.width = context.resources.displayMetrics.widthPixels - 300
        window?.attributes = layoutParams
        initView()
        bindAction()
    }

    private fun initView() {
        positiveButton = findViewById(R.id.tv_cookie_sign_positive)
        negativeButton = findViewById(R.id.tv_cookie_sign_negative)
    }

    private fun bindAction() {
        positiveButton?.setOnClickListener {
            positiveClickListener?.invoke(it)
        }

        negativeButton?.setOnClickListener {
            negativeClickListener?.invoke(it)
        }
    }

    fun setOnClickPositiveButton(listener: (View) -> Unit) {
        positiveClickListener = listener
    }

    fun setOnClickNegativeButton(listener: (View) -> Unit) {
        negativeClickListener = listener
    }
}