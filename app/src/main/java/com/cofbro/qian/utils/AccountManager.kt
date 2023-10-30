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
        jsonObject[Constants.Account.USERNAME] = username
        jsonObject[Constants.Account.PASSWORD] = password
        jsonObject[Constants.Account.UID] = uid
        jsonObject[Constants.Account.FID] = fid
        jsonObject[Constants.Account.PIC_URL] = URL.getAvtarImgPath(uid)
        if (file.exists()) {
            val newSize = data?.getIntExt(Constants.Account.SIZE).takeIf {
                it != -1
            } ?: 0
            val array = data?.getJSONArrayExt(Constants.Account.USERS) ?: JSONArray()
            array[newSize] = jsonObject
            data?.set(Constants.Account.USERS, array)
            data?.set(Constants.Account.SIZE, newSize + 1)
        } else {
            file.createNewFile()
            data = JSONObject()
            val array = JSONArray()
            array[0] = jsonObject
            data[Constants.Account.HISTORY] = "true"
            data[Constants.Account.SIZE] = 1
            data[Constants.Account.USERS] = array
        }
        return data
    }
}