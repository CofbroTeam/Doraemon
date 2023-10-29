package com.cofbro.qian.profile

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.DefaultRepository
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class ProfileViewModel : BaseViewModel<DefaultRepository>() {
    val uid = CacheUtils.cache["uid"] ?: ""
    var logout_dialog:LogoutDialog?  = null
    var userInfoLiveData = ResponseMutableLiveData<Response>()

    fun requestForUserInfo(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(userInfoLiveData) {
                val request = NetworkUtils.buildClientRequest(url)
                NetworkUtils.request(request)
            }
        }
    }

}