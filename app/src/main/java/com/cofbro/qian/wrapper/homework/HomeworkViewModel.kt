package com.cofbro.qian.wrapper.homework

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.DefaultRepository
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import java.io.File

class HomeworkViewModel : BaseViewModel<DefaultRepository>() {
    val encLiveData = ResponseMutableLiveData<Response>()
    val homeworkListHTML = ResponseMutableLiveData<Response>()
    val todoWorkLiveData = ResponseMutableLiveData<Response>()
    val submitHomeworkLiveData = ResponseMutableLiveData<Response>()

    suspend fun requestForEnc(url: String) {
        repository.request(encLiveData, false) {
            val request = NetworkUtils.buildClientRequest(url)
            NetworkUtils.request(request)
        }

    }

    suspend fun requestForHomeworkHTML(url: String) {
        repository.request(homeworkListHTML, false) {
            val request = NetworkUtils.buildClientRequest(url)
            NetworkUtils.request(request)
        }
    }

    fun toDoHomework(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(todoWorkLiveData, false) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }
}