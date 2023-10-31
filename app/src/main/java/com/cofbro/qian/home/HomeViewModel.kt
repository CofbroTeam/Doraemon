package com.cofbro.qian.home

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.DefaultRepository
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class HomeViewModel : BaseViewModel<DefaultRepository>() {
    val loadCourseListLiveData = ResponseMutableLiveData<Response>()
    val signLiveData = ResponseMutableLiveData<Response>()
    val userInfoLiveData = ResponseMutableLiveData<Response>()

    fun requestForUserInfo(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(userInfoLiveData, false) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }


    fun loadCourseList(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(loadCourseListLiveData, false) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }

    fun signWithCamera(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(signLiveData, false) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }
}