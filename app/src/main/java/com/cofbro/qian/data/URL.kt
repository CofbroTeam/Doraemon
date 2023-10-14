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

    // 获取签到类型
    fun getSignType(activeId: String): String =
        "https://mobilelearn.chaoxing.com/newsign/signDetail?activePrimaryId=$activeId&type=1"

    fun getSignCodePath(activeId: String): String =
        "https://mobilelearn.chaoxing.com/v2/apis/active/getPPTActiveInfo?activeId=$activeId&duid=&denc="

    fun getSignWithCameraPath(aid: String): String =
        "https://mobilelearn.chaoxing.com/pptSign/stuSignajax?activeId=$aid"

    // 普通签到
    fun getNormalSignPath(courseId: String, classId: String, aid: String): String =
        "https://mobilelearn.chaoxing.com/widget/sign/pcStuSignController/signIn?courseId=$courseId&classId=$classId&activeId=$aid"

    fun getLocationSignPath(
        address: String,
        aid: String,
        uid: String,
        lat: String,
        long: String,
    ): String =
        "https://mobilelearn.chaoxing.com/pptSign/stuSignajax?address=$address&activeId=$aid&uid=$uid&clientip=0.0.0.0&latitude=$lat&longitude=$long&fid=&appType=15&ifTiJiao=1"

    fun getUploadToken(): String = "https://pan-yz.chaoxing.com/api/token/uservalid"

    fun getUploadImagePath(token: String): String =
        "https://pan-yz.chaoxing.com/upload?_token=$token"

    fun getSignWithPhoto(aid: String, uid: String, objectId: String): String =
        "https://mobilelearn.chaoxing.com/pptSign/stuSignajax?activeId=$aid&uid=$uid&appType=15&fid=0&objectId=$objectId"

}