package com.cofbro.qian.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

object ImageDownloader {
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

    private fun writeToFile(
        inputStream: InputStream,
        filePath: String,
        finish: (Boolean, String) -> Unit
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
}