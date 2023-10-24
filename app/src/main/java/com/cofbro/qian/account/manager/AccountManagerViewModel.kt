package com.cofbro.qian.account.manager

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.account.adapter.AccountsAdapter
import com.cofbro.qian.login.LoginViewModel
import com.cofbro.qian.profile.LogoutDialog
import com.cofbro.qian.utils.DefaultRepository
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class AccountManagerViewModel: BaseViewModel<DefaultRepository>()  {

    val loginLiveData = ResponseMutableLiveData<Response>()


    fun login(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.request(loginLiveData, false) {
                val request = NetworkUtils.buildServerRequest(url)
                NetworkUtils.request(request)
            }

        }
    }

}
class User(val user:String,val pwd:String,val uid:String,val cookie:String,val fid:String)
/**
 *  CacheUtils.cache["uid"] = uid ?: ""
 *                         CacheUtils.cache["cookies"] = cookies.toString()
 *                         CacheUtils.cache["fid"] = fid ?: ""
 */