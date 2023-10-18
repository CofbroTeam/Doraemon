package com.cofbro.qian.wrapper.homework

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.DefaultRepository
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class HomeworkViewModel : BaseViewModel<DefaultRepository>() {
    val homeworkLiveData = ResponseMutableLiveData<Response>()

    fun queryHomeworkList(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(homeworkLiveData, false) {
                val request = NetworkUtils.buildServerRequest(url)
                NetworkUtils.request(request)
            }
        }
    }
}