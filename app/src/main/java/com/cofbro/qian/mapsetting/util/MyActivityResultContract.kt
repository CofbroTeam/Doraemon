package com.cofbro.qian.mapsetting.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.cofbro.qian.mapsetting.InputTipsActivity

class MyActivityResultContract: ActivityResultContract<String, String>(){
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, InputTipsActivity::class.java).apply {
            putExtra("name",input)
        }
    }



    override fun parseResult(resultCode: Int, intent: Intent?): String {
        val data = intent?.getStringExtra("result")
        return if (resultCode == Activity.RESULT_OK && data != null) data
        else "null"
    }

}