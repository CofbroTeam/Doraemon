package com.cofbro.qian.mapsetting.repository

import com.cofbro.hymvvmutils.base.BaseRepository
import com.cofbro.hymvvmutils.base.BaseResponse
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData

class MapRepository : BaseRepository() {
    /**
     * api网络请求
     */
    suspend fun <T : Any> request(
        responseLiveData: ResponseMutableLiveData<T>,
        showLoading: Boolean = true,
        loadingMsg: String? = null,
        block: suspend () -> BaseResponse<T>
    ) {
        executeRequest(responseLiveData, showLoading, loadingMsg, block)
    }
}