package com.cofbro.qian.login

import com.cofbro.hymvvmutils.base.BaseRepository
import com.cofbro.hymvvmutils.base.BaseResponse
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData

class LoginRepository : BaseRepository() {
    suspend fun <T : Any> login(
        responseLiveData: ResponseMutableLiveData<T>,
        showLoading: Boolean = true,
        loadingMsg: String? = null,
        block: suspend () -> BaseResponse<T>
    ) {
        executeRequest(responseLiveData, showLoading, loadingMsg, block)
    }
}