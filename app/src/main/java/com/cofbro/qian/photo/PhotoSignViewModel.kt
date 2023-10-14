package com.cofbro.qian.photo

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import java.io.File

class PhotoSignViewModel : BaseViewModel<PhotoSignRepository>() {
    val tokenLiveData = ResponseMutableLiveData<Response>()
    val uploadImageLiveData = ResponseMutableLiveData<Response>()
    val signLiveData = ResponseMutableLiveData<Response>()

    suspend fun requestToken(url: String) {
        val request = NetworkUtils.buildClientRequest(url)
        repository.request(tokenLiveData) {
            NetworkUtils.request(request)
        }
    }

    fun uploadImage(url: String, file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(uploadImageLiveData) {
                NetworkUtils.post(url, file)
            }
        }
    }

    fun sign(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(signLiveData, loadingMsg = "正在签到") {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }
}