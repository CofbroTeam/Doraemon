package com.cofbro.qian.account.manager

import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.qian.profile.LogoutDialog

class AccountManagerViewModel: BaseViewModel<AccountManagerRepository>()  {
    var accountsList = mutableListOf<User>()
    var dialog : LogoutDialog?  = null
}
class User(val user:String,val pwd:String,val uid:String,val cookie:String,val fid:String)
/**
 *  CacheUtils.cache["uid"] = uid ?: ""
 *                         CacheUtils.cache["cookies"] = cookies.toString()
 *                         CacheUtils.cache["fid"] = fid ?: ""
 */