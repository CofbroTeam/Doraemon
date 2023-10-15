package com.cofbro.qian.utils

import com.cofbro.hymvvmutils.base.BaseRepository
import com.cofbro.hymvvmutils.base.BaseResponse
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData

class DefaultRepository : BaseRepository() {
    suspend fun <T : Any> request(
        responseLiveData: ResponseMutableLiveData<T>,
        showLoading: Boolean = true,
        loadingMsg: String? = null,
        block: suspend () -> BaseResponse<T>
    ) {
        executeRequest(responseLiveData, showLoading, loadingMsg, block)
    }
}