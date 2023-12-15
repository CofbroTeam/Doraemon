package com.cofbro.qian.profile

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.cofbro.qian.R
import com.cofbro.qian.databinding.DialogLogoutBinding

class LogoutDialog(context: Context) :
    AlertDialog(context, R.style.Dialog_Fullscreen) {
    lateinit var binding: DialogLogoutBinding
    private var cancelClick: (() -> Unit?)? = null
    private var confirmClick: (() -> Unit?)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLogoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.btnCancel.setOnClickListener {
            cancelClick?.invoke()
        }
        binding.btnConfirm.setOnClickListener {
            confirmClick?.invoke()
        }
    }

    fun setCancelClickListener(cancelClickListener: () -> Unit) {
        cancelClick = cancelClickListener
    }

    fun setConfirmClickListener(confirmClickListener: () -> Unit) {
        confirmClick = confirmClickListener
    }
}