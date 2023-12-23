package com.cofbro.qian.profile

import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.DefaultRepository

class ProfileViewModel : BaseViewModel<DefaultRepository>() {
    val uid = CacheUtils.cache["uid"] ?: ""
    var logoutDialog:LogoutDialog?  = null

}