package com.cofbro.qian

import android.app.Application
import com.cofbro.hymvvmutils.lean.LeanCloudUtils
import com.hjq.toast.ToastUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
        LeanCloudUtils.init(false)
    }
}