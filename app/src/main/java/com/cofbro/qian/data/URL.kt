package com.cofbro.qian.data

object URL {
    // 登录接口
    fun getLoginPath(username: String, password: String): String =
        "https://passport2-api.chaoxing.com/v11/loginregister?code=$password&cx_xxt_passport=json&uname=$username&loginType=1&roleSelect=true"

    // 获取所有课程
    fun getAllCourseListPath(): String = "http://mooc1-api.chaoxing.com/mycourse/backclazzdata"

    // 获得头像地址
    fun getAvtarImgPath(uid: String): String = "http://photo.chaoxing.com/p/${uid}_80"

    // 查询所有活动
    fun gatActiveTaskListPath(courseId: String, classId: String, uid: String, cpi: String): String =
        "https://mobilelearn.chaoxing.com/ppt/activeAPI/taskactivelist?courseId=$courseId&classId=$classId&uid=$uid&cpi=$cpi"

    // 获取签到方法
    fun getSignType(activeId: String): String =
        "https://mobilelearn.chaoxing.com/newsign/signDetail?activePrimaryId=$activeId&type=1"
}