package com.cofbro.qian.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.io.File

class InstallCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.data?.schemeSpecificPart
        // 判断是否应用程序安装完成
        if (context?.packageName == packageName) {
            // 删除已下载的更新包文件
            val filepath = context?.filesDir?.path + File.separatorChar + "app-release-c.apk"
            val file = File(filepath)
            file.delete()
        }
    }
}