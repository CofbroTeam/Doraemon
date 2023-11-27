package com.cofbro.qian.profile

import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.DefaultRepository
import com.cofbro.qian.utils.NetworkUtils
import okhttp3.Response

class ProfileViewModel : BaseViewModel<DefaultRepository>() {
    val uid = CacheUtils.cache["uid"] ?: ""
    var logoutDialog:LogoutDialog?  = null


}