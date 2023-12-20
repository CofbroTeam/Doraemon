package com.cofbro.qian.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.cofbro.qian.R

class WaitDialog(context: Context) : AlertDialog(context) {
    var csBottom: ConstraintLayout? = null
    var tipSureButton: TextView? = null
    private var positiveButton: TextView? = null
    private var negativeButton: TextView? = null
    private var title: TextView? = null
    private var content: TextView? = null
    private var positiveClickListener: ((View) -> Unit)? = null
    private var negativeClickListener: ((View) -> Unit)? = null
    private var tipSureClickListener: ((View) -> Unit)? = null
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
        tipSureButton = findViewById(R.id.tv_tip_sure)
        csBottom = findViewById(R.id.cs_bottom)
        title = findViewById(R.id.tv_cookie_sign_dialog_title)
        content = findViewById(R.id.tv_cookie_sign_dialog_content)
    }

    private fun bindAction() {
        positiveButton?.setOnClickListener {
            positiveClickListener?.invoke(it)
        }

        negativeButton?.setOnClickListener {
            negativeClickListener?.invoke(it)
        }

        tipSureButton?.setOnClickListener {
            tipSureClickListener?.invoke(it)
        }
    }

    fun setTitle(titleString: String) {
        title?.text = titleString
    }

    fun setContent(contentString: String) {
        content?.text = contentString
    }

    fun setOnClickPositiveButton(listener: (View) -> Unit) {
        positiveClickListener = listener
    }

    fun setOnClickNegativeButton(listener: (View) -> Unit) {
        negativeClickListener = listener
    }

    fun setOnClickTipSureButton(listener: (View) -> Unit) {
        tipSureClickListener = listener
    }
}