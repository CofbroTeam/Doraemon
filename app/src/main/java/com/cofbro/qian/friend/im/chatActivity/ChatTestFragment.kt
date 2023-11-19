package com.cofbro.qian.friend.im.chatActivity

import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.leancloud.im.v2.LCIMConversation
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.qian.databinding.ActivityChatBinding
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.mapsetting.util.ToastUtil

class ChatTestFragment : BaseFragment<ChatViewModel, ActivityChatBinding>() {
    private val list = listOf(
        ChatContent(ChatContent.TYPE_NORMAL_MYSELF, "胡绍鹰", "21:20"),
        ChatContent(ChatContent.TYPE_NORMAL_FRIEND, "陈浩钖", "21:20"),
        ChatContent(ChatContent.TYPE_REQUEST_HOMEWORK_MYSELF, "胡绍鹰发出作业索要", "21:20"),
        ChatContent(ChatContent.TYPE_REQUEST_HOMEWORK_FRIEND, "陈浩钖发出作业索要", "21:20"),
        ChatContent(ChatContent.TYPE_REQUEST_HOMEWORK_FRIEND, "陈浩钖发出作业索要", "21:20"),
        ChatContent(ChatContent.TYPE_REQUEST_HOMEWORK_FRIEND, "陈浩钖发出作业索要", "21:20"),
        ChatContent(ChatContent.TYPE_NORMAL_MYSELF, "胡绍鹰", "21:20"),
    )

    override fun onAllViewCreated(savedInstanceState: Bundle?) {
        setBackClick()
        setRecyclerView()
        setRVList(list)
        setSendClick()
        setRejectAndAcceptListener()
    }

    private fun setBackClick() {
        binding!!.topBack.setOnClickListener {
            //finish()
        }
    }

    private fun setRecyclerView() {
        binding!!.rvChat.apply {
            adapter = ChatAdapter()
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    fun setRVList(list: List<ChatContent>) {
        (binding!!.rvChat.adapter as ChatAdapter).setItemList(list)
    }

    private fun setSendClick() {
        binding!!.send.setOnClickListener { view ->
            if (binding!!.limitInputView.text?.toString() == "") return@setOnClickListener
            val message = binding!!.limitInputView.clearAndSend()
            IMClientUtils.sendMsg(
                viewModel.conversation,
                message,
                { ToastUtil.show(view.context, "发送成功 -> $message") },
                { ToastUtil.show(view.context, "发送失败 -> $it") }
            )
        }
    }


    private fun setRejectAndAcceptListener() {
        (binding!!.rvChat.adapter as ChatAdapter).apply {
            setOnRejectClickListener {
                ToastUtil.show(it.context, "已拒绝给作业")
            }
            setOnAcceptClickListener {
                ToastUtil.show(it.context, "已同意给作业\n正在跳转到给作业详情页...")
            }
        }
    }
}