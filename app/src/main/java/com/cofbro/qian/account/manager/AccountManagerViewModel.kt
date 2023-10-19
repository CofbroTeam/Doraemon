package com.cofbro.qian.account.manager

import com.cofbro.hymvvmutils.base.BaseViewModel

class AccountManagerViewModel: BaseViewModel<AccountManagerRepository>()  {
    var accountsList = arrayListOf<User>()
}
class User(user:String,pwd:String)