package com.cofbro.qian.utils

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
