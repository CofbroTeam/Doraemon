package com.cofbro.qian.task

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class TaskViewModel : BaseViewModel<TaskRepository>() {
    val queryActiveTaskListLiveData = ResponseMutableLiveData<Response>()

    fun queryActiveTaskList(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.queryActiveTaskList(queryActiveTaskListLiveData, false) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }
}