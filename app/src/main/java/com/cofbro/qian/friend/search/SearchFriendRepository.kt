package com.cofbro.qian.friend.search

import cn.leancloud.LCObject
import cn.leancloud.LCUser
import com.cofbro.hymvvmutils.base.BaseRepository

class SearchFriendRepository : BaseRepository()  {
    fun loginLC(username: String, password: String, onSuccess: (LCUser) -> Unit) {
        leanCloudUtils.login(username, password, "", onSuccess)
    }

    fun saveInLC(
        className: String,
        equalMap: HashMap<String, String>,
        onSuccess: (LCObject) -> Unit = {}
    ) {
        leanCloudUtils.saveInLC(className, equalMap, null, onSuccess)
    }

    fun searchInLC(
        className: String,
        equalMap: HashMap<String, String>,
        onSuccess: (List<LCObject>) -> Unit = {}
    ) {
        leanCloudUtils.searchInLC(className, equalMap, null, onSuccess)
    }
}