package com.cofbro.qian.main

import androidx.lifecycle.viewModelScope
import com.cofbro.hymvvmutils.base.BaseViewModel
import com.cofbro.hymvvmutils.base.ResponseMutableLiveData
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.Response

class MainViewModel : BaseViewModel<MainRepository>() {
    companion object {
        const val LOAD_ALL_COURSE = "课程加载中"
    }

    val loadCourseListLiveData = ResponseMutableLiveData<Response>()


    fun loadCourseList(url: String) {
        val cookies = CacheUtils.cache["cookies"] ?: ""
        val request = Request.Builder().url(url)
            .addHeader("Accept-Language", "zh-Hans-CN;q=1, zh-Hant-CN;q=0.9")
            .addHeader("cookie", cookies)
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 14_2_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 com.ssreader.ChaoXingStudy/ChaoXingStudy_3_4.8_ios_phone_202012052220_56 (@Kalimdor)_12787186548451577248"
            )
            .build()
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadCourseList(loadCourseListLiveData, true, LOAD_ALL_COURSE) {
                NetworkUtils.request(url, request)
            }
        }
    }
}