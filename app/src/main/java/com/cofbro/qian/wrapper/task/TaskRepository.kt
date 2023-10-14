package com.cofbro.qian.wrapper.task

import com.cofbro.hymvvmutils.base.BaseRepository
import com.cofbro.hymvvmutils.base.BaseResponse
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData

class TaskRepository : BaseRepository() {
    suspend fun <T : Any> queryActiveTaskList(
        responseLiveData: ResponseMutableLiveData<T>,
        showLoading: Boolean = true,
        loadingMsg: String? = null,
        block: suspend () -> BaseResponse<T>
    ) {
        executeRequest(responseLiveData, showLoading, loadingMsg, block)
    }

    suspend fun <T : Any> request(
        responseLiveData: ResponseMutableLiveData<T>,
        showLoading: Boolean = true,
        loadingMsg: String? = null,
        block: suspend () -> BaseResponse<T>
    ) {
        executeRequest(responseLiveData, showLoading, loadingMsg, block)
    }
}