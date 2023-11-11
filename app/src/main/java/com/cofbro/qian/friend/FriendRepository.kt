package com.cofbro.qian.friend

import cn.leancloud.LCUser
import com.cofbro.hymvvmutils.base.BaseRepository

class FriendRepository : BaseRepository() {
    fun loginLC(username: String, password: String, onSuccess: (LCUser) -> Unit) {
        leanCloudUtils.login(username, password, "", onSuccess)
    }
}