package com.cofbro.qian.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.cofbro.qian.R

class LogoutDialog(context: Context):AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout(30,30)
        setContentView(R.layout.dialog_logout)

    }
}