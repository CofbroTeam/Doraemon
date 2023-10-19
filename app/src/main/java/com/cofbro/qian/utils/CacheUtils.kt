package com.cofbro.qian.utils

import com.cofbro.qian.account.manager.User

object CacheUtils {
    val cache = hashMapOf<String, String>()
    val cacheUser = hashMapOf<String,MutableList<User>>()
}