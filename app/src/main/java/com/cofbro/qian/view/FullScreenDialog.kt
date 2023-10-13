package com.cofbro.qian.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsCompat.Type.InsetsType
import com.cofbro.qian.R

class FullScreenDialog(context: Context) : AlertDialog(context, R.style.Dialog_Fullscreen) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = Color.TRANSPARENT
        window?.navigationBarColor = Color.TRANSPARENT
        val layoutParams = window?.attributes
        window?.setBackgroundDrawable(null)
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
}