package com.cofbro.qian.login.sms

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.DefaultRepository
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class SMSViewModel : BaseViewModel<DefaultRepository>() {
    var smsSendLiveData = ResponseMutableLiveData<Response>()
    var loginLiveData = ResponseMutableLiveData<Response>()

    fun sendSMS(url: String, bodyString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(smsSendLiveData, false) {
                NetworkUtils.postForLogin(bodyString, url)
            }
        }
    }

    fun login(url: String, bodyString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(loginLiveData, false) {
                NetworkUtils.postForLogin(bodyString, url)
            }
        }
    }
}