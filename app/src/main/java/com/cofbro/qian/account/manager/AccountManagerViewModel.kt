package com.cofbro.qian.account.manager

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.DefaultRepository
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class AccountManagerViewModel: BaseViewModel<DefaultRepository>()  {

    val loginLiveData = ResponseMutableLiveData<Response>()


    fun login(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(loginLiveData, false) {
                val request = NetworkUtils.buildServerRequest(url)
                NetworkUtils.request(request)
            }

        }
    }

}