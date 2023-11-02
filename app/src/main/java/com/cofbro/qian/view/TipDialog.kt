package com.cofbro.qian.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.cofbro.qian.R

class TipDialog(context: Context) : AlertDialog(context) {
    private var positiveClick: ((View) -> Unit)? = null
    private var negativeClick: ((View) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_tip)
        val layoutParams = window?.attributes
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        layoutParams?.width = context.resources.displayMetrics.widthPixels - 200
        window?.attributes = layoutParams

        val positiveButton = findViewById<TextView>(R.id.tv_positive)
        val negativeButton = findViewById<TextView>(R.id.tv_negative)

        positiveButton.setOnClickListener {
            positiveClick?.invoke(it)
        }

        negativeButton.setOnClickListener {
            negativeClick?.invoke(it)
        }

    }

    fun setPositiveClickListener(listener: (View) -> Unit) {
        positiveClick = listener
    }

    fun setNegativeClickListener(listener: (View) -> Unit) {
        negativeClick = listener
    }
}