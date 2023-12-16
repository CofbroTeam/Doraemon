package com.cofbro.qian.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.cofbro.qian.R

class DownloadDialog(context: Context) : AlertDialog(context) {
    var progress: ProgressBar? = null
    var textStatus: TextView? = null
    private var tvCancel: TextView? = null
    private var negativeClick: ((View) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_download)
        val layoutParams = window?.attributes
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        layoutParams?.width = context.resources.displayMetrics.widthPixels - 200
        window?.attributes = layoutParams

        initView()

        tvCancel?.setOnClickListener {
            negativeClick?.invoke(it)
        }

    }

    fun setOnNegativeButtonListener(listener: (View) -> Unit) {
        negativeClick = listener
    }

    private fun initView() {
        progress = findViewById(R.id.progressBar)
        tvCancel = findViewById(R.id.tv_download_cancel)
        textStatus = findViewById(R.id.txtStatus)
    }
}