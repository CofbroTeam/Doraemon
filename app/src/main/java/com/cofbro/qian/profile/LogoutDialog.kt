package com.cofbro.qian.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.amap.api.services.help.Tip
import com.cofbro.qian.R
import com.cofbro.qian.databinding.DialogLogoutBinding

class LogoutDialog(context: Context):AlertDialog(context,R.style.Dialog_Fullscreen) {
    lateinit var binding:DialogLogoutBinding
    private var CancelClick: ((itemTip: String) -> Unit?)? = null
    private var ConfirmClick: ((itemTip:String) -> Unit?)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLogoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setLayout(1000,1000)
        binding.btnCancel.setOnClickListener {
            CancelClick?.invoke("se")
        }
        binding.btnConfirm.setOnClickListener {
            ConfirmClick?.invoke("se")
        }

    }

     fun setCancelClickListener(CancelClickListener: (itemTip:String) -> Unit){
            CancelClick = CancelClickListener
    }
     fun setConfirmClickListener(ConfirmClickListener: (itemTip:String) -> Unit){
        ConfirmClick = ConfirmClickListener
    }
}