package com.cofbro.qian.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.hymvvmutils.base.saveUsedSp
import com.cofbro.qian.R
import com.cofbro.qian.utils.Constants

class AutoUpdateTipDialog(context: Context) : AlertDialog(context) {
    private var checkBox: CheckBox? = null
    private var tvUpdateRightNow: TextView? = null
    private var tvUpdateLater: TextView? = null
    private var positiveClick: (() -> Unit)? = null
    private var negativeClick: (() -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_update_tip)
        val layoutParams = window?.attributes
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        layoutParams?.width = context.resources.displayMetrics.widthPixels - 100
        window?.attributes = layoutParams
        initView()
        bindAction()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (checkBox?.isChecked == true) {
            context.saveUsedSp(Constants.Update.UPDATE_TIP, Constants.Update.NO_SHOW)
        } else if (checkBox?.isChecked == false) {
            context.saveUsedSp(Constants.Update.UPDATE_TIP, Constants.Update.SHOW)
        }
    }

    private fun bindAction() {
        tvUpdateRightNow?.setOnClickListener {
            positiveClick?.invoke()
        }

        tvUpdateLater?.setOnClickListener {
            negativeClick?.invoke()
        }
    }

    private fun initView() {
        checkBox = findViewById(R.id.check_box)
        tvUpdateRightNow = findViewById(R.id.tv_update_right_now)
        tvUpdateLater = findViewById(R.id.tv_update_later)
        initCheckBox()
    }

    private fun initCheckBox() {
        val tipShow = context.getBySp(Constants.Update.UPDATE_TIP)
        if (tipShow == Constants.Update.NO_SHOW) {
            checkBox?.isChecked = true
        } else if (tipShow == Constants.Update.SHOW) {
            checkBox?.isChecked = false
        }
    }

    fun setOnPositiveListener(listener: () -> Unit) {
        positiveClick = listener
    }

    fun setOnNegativeListener(listener: () -> Unit) {
        negativeClick = listener
    }
}