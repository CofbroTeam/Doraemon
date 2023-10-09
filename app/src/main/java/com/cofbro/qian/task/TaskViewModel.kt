package com.cofbro.qian.task

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class TaskViewModel : BaseViewModel<TaskRepository>() {
    private val preSignLiveData = ResponseMutableLiveData<Response>()
    val queryActiveTaskListLiveData = ResponseMutableLiveData<Response>()
    val signTypeLiveData = ResponseMutableLiveData<Response>()
    val signLiveData = ResponseMutableLiveData<Response>()
    val signCodeLiveData = ResponseMutableLiveData<Response>()


    fun queryActiveTaskList(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.queryActiveTaskList(queryActiveTaskListLiveData, false) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }

    suspend fun findSignType(url: String) {
        repository.request(signTypeLiveData, false) {
            val request = NetworkUtils.buildClientRequest(url)
            NetworkUtils.request(request)
        }
    }

    suspend fun preSign(url: String) {
        repository.request(preSignLiveData, false) {
            val request = NetworkUtils.buildClientRequest(url)
            NetworkUtils.request(request)
        }
    }

    suspend fun sign(url: String) {
        repository.request(signLiveData, false) {
            val request = NetworkUtils.buildClientRequest(url)
            NetworkUtils.request(request)
        }
    }

    suspend fun getSignCode(url: String) {
        repository.request(signCodeLiveData, false) {
            val request = NetworkUtils.buildServerRequest(url)
            NetworkUtils.request(request)
        }
    }
}