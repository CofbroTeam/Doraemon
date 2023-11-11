package com.cofbro.qian.friend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.leancloud.LCUser
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.DefaultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendViewModel : BaseViewModel<FriendRepository>() {
    val loginIMLiveData = MutableLiveData<LCUser>()
    val traversAdapterLiveData = MutableLiveData<List<JSONObject>>()


    fun loginLC(username: String, password: String, onSuccess: (LCUser) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loginLC(username, password, onSuccess)
        }
    }
}