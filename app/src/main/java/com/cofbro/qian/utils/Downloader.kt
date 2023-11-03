package com.cofbro.qian.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.InputStream


object Downloader {
    fun download(context: Context, uri: String, finish: (Boolean, String) -> Unit) {
        try {
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val path = context.filesDir.path + File.separatorChar + fileName
            if (Build.VERSION.SDK_INT >= 29) {
                context.contentResolver.openInputStream(Uri.parse(uri)).use {
                    it?.let {
                        writeToFile(it, path, finish)
                    }
                }
            } else {
                val inputStream = FileInputStream(File(uri))
                writeToFile(inputStream, path, finish)
            }
        } catch (_: Exception) {
            finish(false, "")
        }
    }

    fun download(context: Context, uri: Uri, finish: (Boolean, String) -> Unit) {
        try {
            val filename = getFileName(context, uri) ?: "default.doc"
            val path = context.filesDir.path + File.separatorChar + filename
            context.contentResolver.openInputStream(uri).use {
                it?.let {
                    writeToFile(it, path, finish)
                }
            }
        } catch (_: Exception) {
            finish(false, "")
        }
    }

    fun download(context: Context, filename: String, json: String) {
        try {
            val filepath = context.filesDir.path + File.separatorChar + filename
            val fileOutputStream = FileOutputStream(File(filepath))
            val bytes: ByteArray = json.toByteArray()
            fileOutputStream.write(bytes)
            fileOutputStream.close()
        } catch (_: Exception) {
        }
    }

    fun acquire(context: Context, filename: String): String {
        var result = ""
        try {
            val filepath = context.filesDir.path + File.separatorChar + filename
            val fileReader = FileReader(filepath)
            val bufferedReader = BufferedReader(fileReader)
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                result += line
            }
            bufferedReader.close()
        } catch (_: Exception) {
        }
        return result
    }

    fun delete(context: Context, filename: String) {
        val file = File(context.filesDir.path + File.separatorChar + filename)
        if (file.exists()) {
            deleteFolder(file)
        }
    }

    fun deleteFolder(folder: File) {
        val files = folder.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    deleteFolder(file)
                } else {
                    file.delete()
                }
            }
        }
        // 删除本身
        folder.delete()
    }

    fun getFileType(filename: String): String {
        return filename.split(".").getOrNull(1) ?: "pdf"
    }

    private fun writeToFile(
        inputStream: InputStream,
        filePath: String,
        finish: (Boolean, String) -> Unit = { _: Boolean, _: String -> }
    ) {
        BufferedInputStream(inputStream).use { bis ->
            BufferedOutputStream(FileOutputStream(filePath)).use { bos ->
                val buffer = ByteArray(1024)
                var len = bis.read(buffer, 0, 1024)
                while (len != -1) {
                    bos.write(buffer, 0, len)
                    len = bis.read(buffer, 0, 1024)
                }
                bos.flush()
                finish(true, filePath)
            }
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }
}