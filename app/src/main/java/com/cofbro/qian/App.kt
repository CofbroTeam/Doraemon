package com.cofbro.qian

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import cn.leancloud.LeanCloud
import com.cofbro.hymvvmutils.lean.LeanCloudUtils
import com.cofbro.qian.update.InstallCompleteReceiver
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.hjq.toast.ToastUtils

class App : Application(), Application.ActivityLifecycleCallbacks {
    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
        LeanCloudUtils.init(true)
        CacheUtils.cache[Constants.DataLoad.FIRST_LOAD] = Constants.DataLoad.UNLOAD
        registerInstallPackageReceiver()
    }

    private fun registerInstallPackageReceiver() {
        val installCompleteReceiver = InstallCompleteReceiver()
        val filter = IntentFilter(Intent.ACTION_PACKAGE_REPLACED)
        filter.addDataScheme("package")
        registerReceiver(installCompleteReceiver, filter)
    //LeanCloud.initializeSecurely(this, "C8NqVi2SeysVgB2AImB7CAFB-gzGzoHsz", "https://c8nqvi2s.lc-cn-n1-shared.com")
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        CacheUtils.activities[Constants.Cache.TOP_ACTIVITY] = activity
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