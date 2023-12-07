package com.cofbro.qian.wrapper.task

import androidx.lifecycle.MutableLiveData
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
    val signTogetherLiveData = ResponseMutableLiveData<Response>()
    val loginLiveData = ResponseMutableLiveData<Response>()
    val analysisLiveData = ResponseMutableLiveData<Response>()
    val cookieSignLiveData = MutableLiveData<String>()


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
    suspend fun findSignType2(url: String) {
        repository.request( ResponseMutableLiveData<Response>(), false) {
            val request = NetworkUtils.buildClientRequest(url)
            NetworkUtils.request(request)
        }
    }

    suspend fun preSign(url: String, cookies: String = "") {
        repository.request(preSignLiveData, false) {
            val request = if (cookies.isEmpty()) {
                NetworkUtils.buildClientRequest(url)
            } else NetworkUtils.buildClientRequest(url, cookies)
            NetworkUtils.request(request)
        }
    }

    suspend fun sign(url: String) {
        repository.request(signLiveData, false) {
            val request = NetworkUtils.buildClientRequest(url)
            NetworkUtils.request(request)
        }
    }

    suspend fun signTogether(url: String, cookies: String) {
        repository.request(signTogetherLiveData, false) {
            val request = NetworkUtils.buildClientRequest(url, cookies)
            NetworkUtils.request(request)
        }
    }

    suspend fun getSignCode(url: String) {
        repository.request(signCodeLiveData, false) {
            val request = NetworkUtils.buildClientRequest(url)
            NetworkUtils.request(request)
        }
    }

    fun tryLogin(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(loginLiveData, false) {
                val request = NetworkUtils.buildServerRequest(url)
                NetworkUtils.request(request)
            }
        }
    }

    fun tryLoginWithCookies(cookie: String) {
        cookieSignLiveData.postValue(cookie)
    }

    suspend fun request(url: String, cookies: String = "") {
        repository.request(ResponseMutableLiveData(), false) {
            val request = if (cookies.isEmpty()) {
                NetworkUtils.buildClientRequest(url)
            } else NetworkUtils.buildClientRequest(url, cookies)
            NetworkUtils.request(request)
        }
    }

    suspend fun analysis(url: String) {
        repository.request(analysisLiveData, false) {
            val request = NetworkUtils.buildClientRequest(url)
            NetworkUtils.request(request)
        }
    }

    suspend fun analysis2(url: String, cookies: String = "") {
        repository.request(ResponseMutableLiveData(), false) {
            val request = if (cookies.isEmpty()) {
                NetworkUtils.buildClientRequest(url)
            } else NetworkUtils.buildClientRequest(url, cookies)
            NetworkUtils.request(request)
        }
    }

    fun analysisForSignTogether(url: String, cookies: String, onSuccess: (Response) -> Unit = {}, onFailure: (String) -> Unit = {}) {
        val request = NetworkUtils.buildClientRequest(url, cookies)
        NetworkUtils.requestAsync(request, onSuccess, onFailure)
    }
}