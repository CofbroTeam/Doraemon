package com.cofbro.qian.data

object URL {
    fun getLoginPath(username: String, password: String): String =
        "https://passport2-api.chaoxing.com/v11/loginregister?code=$password&cx_xxt_passport=json&uname=$username&loginType=1&roleSelect=true"

    fun getAllCourseListPath(): String = "http://mooc1-api.chaoxing.com/mycourse/backclazzdata"
}