package com.cofbro.qian.utils

import android.annotation.SuppressLint
import android.content.Context
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date

/**
 * {
 *     "size": "2"
 *     "records": [
 *         {
 *             "uid": "123456789",
 *             "courseName": "数学",
 *             "time": "2023.10.31 11:00:00",
 *             "status": "success"
 *         },
 *         {
 *             "uid": "123465789",
 *             "courseName": "语文",
 *             "time": "2023.10.31 11:00:00",
 *             "status": "success"
 *         }
 *       ]
 * }
 */
object SignRecorder {
    private var data: JSONObject? = null

    fun init(context: Context) {
        data = readRecords(context)
    }

    /**
     * 记录只是保存在了内存中，
     * 并没有写入本地
     */
    fun record(context: Context, username: String, courseName: String, status: String) {
        val time = getCurrentTime()
        val data = JSONObject()
        data["uid"] = username
        data["courseName"] = courseName
        data["time"] = time
        data["status"] = status
        insertRecord(context, data)
    }

    fun readRecords(context: Context): JSONObject {
        return Downloader.acquire(context, "sign_record")
            .safeParseToJson()
    }

    private fun insertRecord(context: Context, newData: JSONObject) {
        val filepath = context.filesDir.path + File.separatorChar + "sign_record"
        val file = File(filepath)
        if (file.exists()) {
            val newSize = data?.getIntExt("size").takeIf {
                it != -1
            } ?: 0
            val array = data?.getJSONArrayExt("records") ?: JSONArray()
            array[newSize] = newData
            data?.set("records", array)
            data?.set("size", newSize + 1)
        } else {
            file.createNewFile()
            data = JSONObject()
            val array = JSONArray()
            array[0] = newData
            data?.set("size", 1)
            data?.set("records", array)
        }
    }

    fun writeJson(context: Context) {
        try {
            data?.let {
                val fileWriter = FileWriter(context.filesDir.path + "/sign_record")
                data?.writeJSONString(fileWriter)
                fileWriter.close()
            }
        } catch (_: Exception) {
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(currentDate)
    }
}