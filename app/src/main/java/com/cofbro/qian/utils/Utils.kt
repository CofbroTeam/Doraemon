package com.cofbro.qian.utils

import android.content.Context
import com.hjq.toast.ToastUtils


fun String.showSignResult() {
    var toast = ""
    toast = if (this.contains("success") || this.contains("签到成功")) {
        "签到成功"
    } else if (contains("签到过了")) {
        "您已经签到过啦~"
    } else {
        "签到失败!"
    }
    ToastUtils.show(toast)
}

fun getStatusBarHeight(context: Context): Int {
    var result = dp2px(context, 37)
    try {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
    } catch (_: Exception){}
    return result
}

