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


    fun request(url: String, newRequest: Request? = null): BaseResponse<Response> {
        var request: Request? = newRequest
        if (request == null) {
            request = Request.Builder().url(url).build()
        }
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