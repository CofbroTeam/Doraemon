package com.cofbro.qian.wrapper.task.holder

import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.data.URL
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.NetworkUtils
import com.cofbro.qian.utils.getStringExt
import com.hjq.toast.ToastUtils
import okhttp3.Response
import java.lang.Thread.sleep
import java.util.concurrent.Callable

/**
 * @author chy 2023.12.19
 */
class Task(private val user: JSONObject) : Callable<Result?> {
    // 编码后的位置信息
    var location = ""

    // 二维码读出的信息
    var qrCodeId = ""

    // 签到的aid
    var aid = ""
    var courseId = ""
    var classId = ""

    // 签到密码
    var code = ""
    var preSignUrl = ""

    // 用户cookie
    private var cookies = ""

    override fun call(): Result? {
        tryLoginIfNecessary()
        return sign()
    }

    /**
     * 账号密码登录使用最新的cookie
     */
    private fun tryLoginIfNecessary() {
        val username = user.getStringExt(Constants.Account.USERNAME)
        val password = user.getStringExt(Constants.Account.PASSWORD)
        cookies = if (username.isNotEmpty() && password.isNotEmpty()) {
            val response = requestWithServer(URL.getLoginPath(username, password))
            val headers = response?.headers
            headers?.values("Set-Cookie").toString()
        } else {
            user.getStringExt(Constants.Account.COOKIE)
        }
    }

    /**
     * 签到 -> 内部分为几个步骤
     */
    private fun sign(): Result? {
        val response = analysis(URL.getAnalysisPath(aid))
        return if (response?.code == 200) {
            val data = response.body?.string()
            analysis2(data)
            sleep(200)
            preSign()
            checkSign()
            Result(signReally()?.body?.string(), cookies, user.getStringExt(Constants.Account.REMARK))
        } else {
            ToastUtils.show(response?.message.toString())
            null
        }
    }

    private fun analysis(url: String): Response? {
        val request = NetworkUtils.buildClientRequest(url, cookies)
        return NetworkUtils.request(request).data
    }

    private fun analysis2(data: String?) {
        val analysis2Code = data?.substringAfter("code='+'")?.substringBefore("'") ?: ""
        request(URL.getAnalysis2Path(analysis2Code))
    }

    private fun preSign() {
        val uid = findUID(cookies)
        val signWithPreSign = preSignUrl.substringBefore("uid=") + "uid=$uid"
        request(signWithPreSign)
    }

    private fun checkSign() {
        request(URL.checkSignCodePath(aid, code))
    }

    private fun signReally(): Response? {
        return if (qrCodeId.isNotEmpty()) {
            request(URL.getSignWithCameraPath(qrCodeId, location) + "&uid=${findUID(cookies)}")
        } else {
            request(URL.getNormalSignPath(courseId, classId, aid, code))
        }
    }

    // 网络通用请求
    private fun request(url: String): Response? {
        val request = NetworkUtils.buildClientRequest(url, cookies)
        return NetworkUtils.request(request).data
    }

    private fun requestWithServer(url: String): Response? {
        val request = NetworkUtils.buildServerRequest(url)
        return NetworkUtils.request(request).data
    }

    private fun findUID(cookies: String): String {
        val uid = cookies.substringAfter("UID=")
        return uid.substringBefore(";")
    }
}