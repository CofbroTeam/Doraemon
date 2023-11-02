package com.cofbro.qian.login

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.DefaultRepository
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

open class LoginViewModel : BaseViewModel<LoginRepository>() {
    companion object {
        private const val LOGIN_MESSAGE = "登录中"
    }

    val loginLiveData = ResponseMutableLiveData<Response>()


    fun login(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.login(loginLiveData, loadingMsg = LOGIN_MESSAGE) {
                val request = NetworkUtils.buildServerRequest(url)
                NetworkUtils.request(request)
            }

        }
    }
}