package com.cofbro.qian.friend.chatActivity

import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.leancloud.im.v2.LCIMMessage
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.databinding.ActivityChatBinding
import com.cofbro.qian.friend.chatActivity.observer.ConversationObservable
import com.cofbro.qian.friend.chatActivity.observer.IConversationObserver
import com.cofbro.qian.friend.chatActivity.rv_chat.ChatAdapter
import com.cofbro.qian.friend.chatActivity.rv_chat.ChatContent
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.mapsetting.util.ToastUtil

class ChatActivity : BaseActivity<ChatViewModel, ActivityChatBinding>(), IConversationObserver {
    private val list = mutableListOf(
        ChatContent(ChatContent.TYPE_NORMAL_MYSELF, "胡绍鹰", "21:20"),
        ChatContent(ChatContent.TYPE_NORMAL_FRIEND, "陈浩钖", "21:20"),
        ChatContent(ChatContent.TYPE_REQUEST_HOMEWORK_MYSELF, "胡绍鹰发出作业索要", "21:20"),
        ChatContent(ChatContent.TYPE_REQUEST_HOMEWORK_FRIEND, "陈浩钖发出作业索要", "21:20"),
        ChatContent(ChatContent.TYPE_REQUEST_HOMEWORK_FRIEND, "陈浩钖发出作业索要", "21:20"),
        ChatContent(ChatContent.TYPE_REQUEST_HOMEWORK_FRIEND, "陈浩钖发出作业索要", "21:20"),
        ChatContent(ChatContent.TYPE_NORMAL_MYSELF, "胡绍鹰", "21:20"),
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        ConversationObservable.getInstance().addConversationObserver(this) // 订阅
        setBackClick()
        setRecyclerView()
        setRVList(list)
        setSendClick()
        setRejectAndAcceptListener()
    }

    override fun onResume() {
        super.onResume()
        val rvChatItemCounts = (binding?.rvChat?.adapter as ChatAdapter).getItemCounts()
        if (rvChatItemCounts > 0) {
            binding?.rvChat?.scrollToPosition(rvChatItemCounts - 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ConversationObservable.getInstance().removeConversationObserver()
    }

    private fun setBackClick() {
        binding?.topBack?.setOnClickListener {
            finish()
        }
    }

    private fun setRecyclerView() {
        binding?.rvChat?.apply {
            adapter = ChatAdapter()
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    fun setRVList(list: List<ChatContent>) {
        (binding?.rvChat?.adapter as ChatAdapter).setItemList(list)
    }

    private fun setSendClick() {
        binding?.send?.setOnClickListener { view ->
            if (binding?.limitInputView?.text?.toString() == "") return@setOnClickListener
            val message = binding?.limitInputView?.clearAndSend()
            if (message != null) {
                IMClientUtils.sendMsg(
                    viewModel.mConversation,
                    message,
                    { ToastUtil.show(view.context, "发送成功 -> $message") },
                    { ToastUtil.show(view.context, "发送失败 -> $it") }
                )
            }
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

    override fun getConversationId(): String {
        return viewModel.mConversation.conversationId
    }

    override fun onMessage(message: LCIMMessage) {
        // todo --- onMessage...
        list.add(
            ChatContent(
                ChatContent.TYPE_NORMAL_FRIEND,
                message.content,
                message.timestamp.toString()
            )
        )
        setRVList(list)
        binding?.rvChat?.adapter?.notifyItemInserted(list.size - 1)
    }
}