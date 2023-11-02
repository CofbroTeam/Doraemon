package com.cofbro.qian.record

import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.qian.utils.DefaultRepository

class SignRecordViewModel : BaseViewModel<DefaultRepository>() {
    val loadLiveData = MutableLiveData<JSONObject>()

    fun notifyData() {
        loadLiveData.postValue(JSONObject())
    }
}