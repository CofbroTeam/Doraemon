package com.cofbro.qian.main

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.Response

class MainViewModel : BaseViewModel<MainRepository>() {
    companion object {
        const val LOAD_ALL_COURSE = "课程加载中"
    }

    val loadCourseListLiveData = ResponseMutableLiveData<Response>()
    val queryActiveTaskListLiveData = ResponseMutableLiveData<Response>()


    fun loadCourseList(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadCourseList(loadCourseListLiveData, true, LOAD_ALL_COURSE) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }

    fun queryActiveTaskList(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.queryActiveTaskList(queryActiveTaskListLiveData, false) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }
}