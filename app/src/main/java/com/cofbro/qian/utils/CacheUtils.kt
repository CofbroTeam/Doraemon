package com.cofbro.qian.utils

import android.app.Activity
import cn.leancloud.im.v2.LCIMConversation


object CacheUtils {
    val cache = hashMapOf<String, String>()
    val activities = hashMapOf<String, Activity>()
    val conv = hashMapOf<String, LCIMConversation>()
}