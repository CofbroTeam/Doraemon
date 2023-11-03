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

    fun buildAccount(username: String, password: String, uid: String, fid: String, cookies:String): JSONObject {
        val data = JSONObject()
        data[Constants.Account.USERNAME] = username
        data[Constants.Account.PASSWORD] = password
        data[Constants.Account.UID] = uid
        data[Constants.Account.COOKIE] = cookies
        data[Constants.Account.FID] = fid
        data[Constants.Account.PIC_URL] = URL.getAvtarImgPath(uid)
        return data
    }

    fun bindAccounts(context: Context, loadedData: JSONObject?, newData: JSONObject): JSONObject? {
        var data = loadedData
        val path = context.filesDir.path + File.separatorChar + Constants.RecycleJson.ACCOUNT_JSON_DATA
        val file = File(path)
        if (file.exists()) {
            val newSize = data?.getIntExt(Constants.Account.SIZE).takeIf {
                it != -1
            } ?: 0
            val array = data?.getJSONArrayExt(Constants.Account.USERS) ?: JSONArray()
            array[newSize] = newData
            data?.set(Constants.Account.USERS, array)
            data?.set(Constants.Account.SIZE, newSize + 1)
        } else {
            file.createNewFile()
            data = JSONObject()
            val array = JSONArray()
            array[0] = newData
            data[Constants.Account.HISTORY] = "true"
            data[Constants.Account.SIZE] = 1
            data[Constants.Account.USERS] = array
        }
        return data
    }
}