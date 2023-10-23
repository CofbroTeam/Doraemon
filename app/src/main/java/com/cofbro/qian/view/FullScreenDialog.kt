package com.cofbro.qian.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import com.cofbro.qian.R
import com.hjq.toast.ToastUtils
import java.util.Timer
import java.util.TimerTask

class FullScreenDialog(context: Context) : AlertDialog(context, R.style.Dialog_Fullscreen) {
    private var timer: Timer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window?.decorView?.setPadding(0, 0, 0, 0)
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = Color.TRANSPARENT
        window?.navigationBarColor = Color.TRANSPARENT
        val layoutParams = window?.attributes
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 设置导航栏颜
        window?.navigationBarColor = Color.TRANSPARENT
        // 内容扩展到导航栏
        window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
        if (Build.VERSION.SDK_INT >= 28) {
            layoutParams?.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window?.attributes = layoutParams

    }

    private fun getLayoutResId(): Int = R.layout.dialog_full_screen

    override fun dismiss() {
        timer?.cancel()
        timer = null
        super.dismiss()
    }

    override fun show() {
        super.show()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    dismiss()
                    ToastUtils.show("请求失败!")
                }
            }
        }, 10000L)
    }
}