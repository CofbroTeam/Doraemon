package com.cofbro.qian

import android.app.Application
import cn.leancloud.LeanCloud
import com.cofbro.hymvvmutils.lean.LeanCloudUtils
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.hjq.toast.ToastUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
        LeanCloudUtils.init(true)
        CacheUtils.cache[Constants.DataLoad.FIRST_LOAD] = Constants.DataLoad.UNLOAD
        LeanCloud.initialize(this, "C8NqVi2SeysVgB2AImB7CAFB-gzGzoHsz", "4igEa01KN9djPGNg2meodKtt", "https://c8nqvi2s.lc-cn-n1-shared.com");
    }
}