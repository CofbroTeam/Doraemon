package com.cofbro.qian.utils

object Constants {
    /**
     * 签到类型
     */
    object SIGN {
        // 普通签到(包含图片签到)
        const val NORMAl = "0"
        // 细分是否图片签到
        const val PHOTO = "1"
        // 扫码签到
        const val SCAN_QR = "2"
        // 手势签到
        const val GESTURE = "3"
        // 定位签到
        const val LOCATION = "4"
        // 签到码签到
        const val SIGN_CODE = "5"
    }

    /**
     * 签到活动状态
     */
    object STATUS {
        const val OPEN = "1"
        const val CLOSE = "2"
    }

    // 活动类型
    object ACTIVITY {
        const val SIGN = "2"
    }
}