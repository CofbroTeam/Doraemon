package com.cofbro.qian.friend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.leancloud.LCObject
import cn.leancloud.LCUser
import cn.leancloud.im.v2.LCIMConversation
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendViewModel : BaseViewModel<FriendRepository>() {
    val loginIMLiveData = MutableLiveData<LCUser>()
    val realConversationLiveData = MutableLiveData<List<JSONObject>>()
    val friendRequestLiveData = MutableLiveData<List<LCIMConversation>>()


    fun loginLC(username: String, password: String, onSuccess: (LCUser) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loginLC(username, password, onSuccess)
        }
    }

    fun saveFriendRelation(
        equalMap: HashMap<String, String>,
        onSuccess: (LCObject) -> Unit = {}
    ) {
        repository.saveInLC("Relation", equalMap, onSuccess)
    }
}