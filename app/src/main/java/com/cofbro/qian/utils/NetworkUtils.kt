package com.cofbro.qian.utils

import com.cofbro.hymvvmutils.base.BaseResponse
import com.cofbro.hymvvmutils.base.DataState
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


object NetworkUtils {
    private val client = OkHttpClient()

    fun buildClientRequest(url: String): Request {
        val cookies = CacheUtils.cache["cookies"] ?: ""
        return Request.Builder().url(url)
            .addHeader("Accept-Language", "zh-Hans-CN;q=1, zh-Hant-CN;q=0.9")
            .addHeader("cookie", cookies)
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 14_2_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 com.ssreader.ChaoXingStudy/ChaoXingStudy_3_4.8_ios_phone_202012052220_56 (@Kalimdor)_12787186548451577248"
            )
            .build()
    }

    fun request(clientRequest: Request): BaseResponse<Response> {
        val call = client.newCall(clientRequest)
        val response = BaseResponse<Response>()
        response.dataState = DataState.STATE_INITIALIZE
        response.data = call.execute()
        return response
    }

    fun request(url: String): BaseResponse<Response> {
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)
        val response = BaseResponse<Response>()
        response.dataState = DataState.STATE_INITIALIZE
        response.data = call.execute()
        return response
    }

    fun requestAsync(url: String, onSuccess: (Response) -> Unit = {}, onFailure: () -> Unit = {}) {
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                onSuccess(response)
            }
        })
    }

}