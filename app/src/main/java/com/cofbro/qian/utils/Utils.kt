package com.cofbro.qian.utils

import com.hjq.toast.ToastUtils


fun String.showSignResult() {
    var toast = ""
    if (this.contains("success") || this.contains("签到成功")) {
        toast = "签到成功"
    } else if (contains("签到过了")) {
        toast = "您已经签到过啦~"
    } else if (contains("失败")) {
        toast = "签到失败!"
    }
    ToastUtils.show(toast)
}
