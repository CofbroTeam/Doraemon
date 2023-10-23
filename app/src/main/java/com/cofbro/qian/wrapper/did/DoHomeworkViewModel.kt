package com.cofbro.qian.wrapper.did

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.DefaultRepository
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import java.io.File

class DoHomeworkViewModel : BaseViewModel<DefaultRepository>() {
    val submitHomeworkLiveData = ResponseMutableLiveData<Response>()
    val attachFileLiveData = ResponseMutableLiveData<Response>()

    fun submitHomework(url: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(submitHomeworkLiveData, false) {
                NetworkUtils.post(url, text)
            }
        }
    }

    fun attachFile(url: String, file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(attachFileLiveData,false){
                NetworkUtils.post2(url, file)
            }
        }
    }
}