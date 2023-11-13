package com.cofbro.qian

import android.app.Activity
import android.app.Application
import android.os.Bundle
import cn.leancloud.LeanCloud
import com.cofbro.hymvvmutils.lean.LeanCloudUtils
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.hjq.toast.ToastUtils

class App : Application(), Application.ActivityLifecycleCallbacks {
    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
        LeanCloudUtils.init(true)
        CacheUtils.cache[Constants.DataLoad.FIRST_LOAD] = Constants.DataLoad.UNLOAD

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        CacheUtils.activities["topActivity"] = activity
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}