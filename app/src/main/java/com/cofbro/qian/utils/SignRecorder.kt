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
        val size = data?.getIntExt(Constants.Recorder.SIZE) ?: 0
        if (size >= 50) {
            data = JSONObject()
            clear(context)
        }
    }

    /**
     * 记录只是保存在了内存中，
     * 并没有写入本地
     */
    fun record(context: Context, username: String, courseName: String, status: String) {
        val time = getCurrentTime()
        val data = JSONObject()
        data[Constants.Recorder.UID] = username
        data[Constants.Recorder.COURSE_NAME] = courseName
        data[Constants.Recorder.TIME] = time
        data[Constants.Recorder.STATUS] = status
        insertRecord(context, data)
    }

    fun readRecords(context: Context): JSONObject {
        return Downloader.acquire(context, Constants.Recorder.FILE_NAME)
            .safeParseToJson()
    }

    private fun insertRecord(context: Context, newData: JSONObject) {
        val filepath = context.filesDir.path + File.separatorChar + Constants.Recorder.FILE_NAME
        val file = File(filepath)
        if (file.exists()) {
            val newSize = data?.getIntExt(Constants.Recorder.SIZE).takeIf {
                it != -1
            } ?: 0
            val array = data?.getJSONArrayExt(Constants.Recorder.RECORDS) ?: JSONArray()
            array[newSize] = newData
            data?.set(Constants.Recorder.RECORDS, array)
            data?.set(Constants.Recorder.SIZE, newSize + 1)
        } else {
            file.createNewFile()
            data = JSONObject()
            val array = JSONArray()
            array[0] = newData
            data?.set(Constants.Recorder.SIZE, 1)
            data?.set(Constants.Recorder.RECORDS, array)
        }
    }

    fun writeJson(context: Context) {
        try {
            data?.let {
                val fileWriter = FileWriter(context.filesDir.path + File.separatorChar + Constants.Recorder.FILE_NAME)
                data?.writeJSONString(fileWriter)
                fileWriter.close()
            }
        } catch (_: Exception) {
        }
    }

    private fun clear(context: Context) {
        Downloader.deleteFolder(File(context.filesDir.path + File.separatorChar + Constants.Recorder.FILE_NAME))
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(currentDate)
    }
}