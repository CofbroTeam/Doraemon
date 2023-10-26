package com.cofbro.qian.utils

import android.content.Context
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.data.URL
import java.io.File

object AccountManager {
    fun loadAllAccountData(context: Context): JSONObject {
        return Downloader.acquire(context, Constants.RecycleJson.ACCOUNT_JSON_DATA)
            .safeParseToJson()
    }

    fun updateAccountData(context: Context, data: String) {
        Downloader.download(context, Constants.RecycleJson.ACCOUNT_JSON_DATA, data)
    }

    fun buildAccount(context: Context, loadedData: JSONObject?, username: String, password: String, uid: String, fid: String): JSONObject? {
        var data = loadedData
        val path = context.filesDir.path + File.separatorChar + Constants.RecycleJson.ACCOUNT_JSON_DATA
        val file = File(path)
        val jsonObject = JSONObject()
        jsonObject["username"] = username
        jsonObject["password"] = password
        jsonObject["uid"] = uid
        jsonObject["fid"] = fid
        jsonObject["picUrl"] = URL.getAvtarImgPath(uid)
        if (file.exists()) {
            val newSize = data?.getIntExt("size") ?: 0
            val array = data?.getJSONArrayExt("users") ?: JSONArray()
            array[newSize] = jsonObject
            data?.set("users", array)
            data?.set("size", newSize + 1)
        } else {
            data = JSONObject()
            val array = JSONArray()
            array[0] = jsonObject
            data["history"] = "true"
            data["size"] = 1
            data["users"] = array
        }
        return data
    }
}