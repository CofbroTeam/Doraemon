package com.cofbro.qian.utils

import android.app.Activity


object CacheUtils {
    val cache = hashMapOf<String, String>()
    val activities = hashMapOf<String, Activity>()
}