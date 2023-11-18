package com.cofbro.qian.utils

import android.content.Context
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.hjq.toast.ToastUtils
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.regex.Matcher
import java.util.regex.Pattern

val monthArray =
    arrayOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
fun String.showSignResult() {
    var toast = ""
    toast = if (this.contains("success") || this.contains("签到成功")) {
        "签到成功"
    } else if (contains("签到过了")) {
        "您已经签到过啦~"
    } else {
        "签到失败!"
    }
    ToastUtils.show(toast)
}

fun getStatusBarHeight(context: Context): Int {
    var result = dp2px(context, 37)
    try {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
    } catch (_: Exception){}
    return result
}

fun splitDateStr(date: String): List<String> {
    val list = arrayListOf<String>()
    val splitTime = date.split(" ").getOrNull(0) ?: ""
    val dateSplit = splitTime.split("-")
    val year = dateSplit.getOrNull(0) ?: ""
    val month = dateSplit.getOrNull(1)?.toInt() ?: 0
    val day = dateSplit.getOrNull(2) ?: ""
    list.add(year)
    list.add(monthArray[month - 1])
    list.add(day)
    list.add(month.toString())
    return list
}

fun Context.saveJsonArraySp(key: String, value: List<*>){
    val json = JSONArray()
    json.addAll(value)
    val editor = getSharedPreferences("sp_data", Context.MODE_PRIVATE).edit()
    editor.putString(key, json.toJSONString()).apply()

}
fun Context.getJsonArraySp(key: String): String? {
    val sp = getSharedPreferences("sp_data", Context.MODE_PRIVATE)
    return sp.getString(key, "")
}
 fun urlEncodeChinese(urlString: String): String {
    var url = urlString
    try {
        val matcher: Matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(url)
        var tmp = ""
        while (matcher.find()) {
            tmp = matcher.group()
            url = url.replace(tmp.toRegex(), URLEncoder.encode(tmp, "UTF-8"))
        }
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    return url.replace(" ", "%20")
}


