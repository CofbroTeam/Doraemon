package com.cofbro.qian.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.cofbro.qian.R

class TipDialog(context: Context) : AlertDialog(context) {
    private var positiveButton: TextView? = null
    private var negativeButton: TextView? = null
    private var title: TextView? = null
    private var content: TextView? = null
    private var positiveClick: ((View) -> Unit)? = null
    private var negativeClick: ((View) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_tip)
        val layoutParams = window?.attributes
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        layoutParams?.width = context.resources.displayMetrics.widthPixels - 200
        window?.attributes = layoutParams

        initView()

        positiveButton?.setOnClickListener {
            positiveClick?.invoke(it)
        }

        negativeButton?.setOnClickListener {
            negativeClick?.invoke(it)
        }

    }

    fun setTitle(titleText: String) {
        title?.text = titleText
    }

    fun setContent(text: String) {
        content?.text = text
    }

    fun setPositiveClickListener(listener: (View) -> Unit) {
        positiveClick = listener
    }

    fun setNegativeClickListener(listener: (View) -> Unit) {
        negativeClick = listener
    }

    private fun initView() {
        positiveButton = findViewById(R.id.tv_positive)
        negativeButton = findViewById(R.id.tv_negative)
        title = findViewById(R.id.tv_tip_dialog_title)
        content = findViewById(R.id.tv_tip_dialog_content)
    }
}