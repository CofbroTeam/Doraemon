package com.cofbro.qian.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.cofbro.qian.R

class LogoutDialog(context: Context):AlertDialog(context,R.style.Dialog_Fullscreen) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_logout)
        window?.setLayout(1000,1000)


    }
}