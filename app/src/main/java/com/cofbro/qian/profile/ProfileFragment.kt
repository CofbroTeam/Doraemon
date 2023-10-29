package com.cofbro.qian.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.MarginLayoutParams
import android.widget.RadioButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.hymvvmutils.base.saveUsedSp
import com.cofbro.qian.account.manager.AccountManagerActivity
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.FragmentProfileBinding
import com.cofbro.qian.login.LoginActivity
import com.cofbro.qian.utils.HtmlParser
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getStatusBarHeight
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.utils.safeParseToJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding>() {
    override fun onAllViewCreated(savedInstanceState: Bundle?) {
        initObserver()
        doNetWork()
        initView()
        initEvent()
    }

    private fun initObserver() {
        viewModel.userInfoLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val data = it.data?.body?.string() ?: ""
                withContext(Dispatchers.Main) {
                    binding?.tvProfileUsername?.text = HtmlParser.parseToUsername(data)
                }
            }
        }
    }

    private fun doNetWork() {
        viewModel.requestForUserInfo("http://i.chaoxing.com/base")
    }

    private fun initView() {
        adjustMarginOfView()
        profileMessageInfo()
        checkSignWithStatus()
    }

    private fun checkSignWithStatus() {
        val switch = requireActivity().getBySp("signWith")?.toBoolean() ?: false
        binding?.signWithButton?.isChecked = switch
    }

    private fun adjustMarginOfView() {
        val statusBarHeight = getStatusBarHeight(requireContext())
        val layoutParams = binding?.csMyInfo?.layoutParams as? MarginLayoutParams
        layoutParams?.topMargin = statusBarHeight + dp2px(requireContext(), 5)
    }
    private fun profileMessageInfo(){
        viewModel.uid.let {
            val options = RequestOptions().transform(
                CenterCrop(),
                RoundedCorners(dp2px(requireContext(), 5))
            )
            Glide.with(this)
                .load(URL.getAvtarImgPath(it))
                .apply(options)
                .into(binding!!.ivProfileUserIcon)
        }
    }
    private fun initEvent(){
        binding?.tvLogin?.setOnClickListener {
            /**
             * 登出出现dialog
             */
            viewModel.logout_dialog = LogoutDialog(requireContext()).apply {
                this.setCancelClickListener {
                    viewModel.logout_dialog?.dismiss()
                }
                this.setConfirmClickListener {
                    /**
                     * dialog 清除数据中，并回到主登录界面
                     */
                    val intent = Intent(requireActivity(),LoginActivity::class.java)
                    clearUserInfo(context)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            viewModel.logout_dialog?.setCancelable(false)
            viewModel.logout_dialog?.show()
        }
        binding?.cowithprofiles?.setOnClickListener {
            val intent = Intent(requireActivity(), AccountManagerActivity::class.java)
            startActivity(intent)
        }

        binding?.signWithButton?.setOnClickListener {
            val switch = it as SwitchCompat
            requireActivity().saveUsedSp("signWith", switch.isChecked.toString())
        }
    }
    private fun clearUserInfo(context: Context){
        context.saveUsedSp("username", "")
        context.saveUsedSp("password", "")
        Toast.makeText(context, "数据删除成功", Toast.LENGTH_SHORT).show()
    }

}