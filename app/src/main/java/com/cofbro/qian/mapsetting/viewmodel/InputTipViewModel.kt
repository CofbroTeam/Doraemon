package com.cofbro.qian.mapsetting.viewmodel

import com.amap.api.services.help.Tip
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.qian.mapsetting.adapter.InputTipsAdapter
import com.cofbro.qian.mapsetting.repository.InputTipRepository

class InputTipViewModel :BaseViewModel<InputTipRepository>(){
     var mCurrentTipList: MutableList<Tip>? = null
     var mIntipAdapter: InputTipsAdapter? = null
}