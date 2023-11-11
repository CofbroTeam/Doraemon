package com.cofbro.qian.friend

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import cn.leancloud.LCObject
import cn.leancloud.im.v2.LCIMClient
import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMMessage
import com.alibaba.fastjson.JSONObject
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.hymvvmutils.base.SP_PASSWORD
import com.cofbro.hymvvmutils.base.SP_USER_NAME
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.qian.databinding.FragmentFriendBinding
import com.cofbro.qian.friend.adapter.MessageListAdapter
import com.cofbro.qian.friend.adapter.UserListAdapter
import com.cofbro.qian.friend.im.IEventCallback
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.friend.im.IMEventManager
import com.cofbro.qian.utils.dp2px
import com.hjq.toast.ToastUtils


class FriendFragment : BaseFragment<FriendViewModel, FragmentFriendBinding>(), IEventCallback {
    private var userListAdapter: UserListAdapter? = null
    private var messageListAdapter: MessageListAdapter? = null
    override fun onAllViewCreated(savedInstanceState: Bundle?) {
        initEventManager()
        initView()
        initObserver()
        doNetwork()
    }

    private fun initObserver() {
        // 登录即时通讯服务
        viewModel.loginIMLiveData.observe(this) {
            queryConversation(
                onSuccess = {
                    traversToAdapterData(it)
                },
                onError = {

                }
            )
        }

        /**
         * data分为两部分获取
         * 1 -> conv
         * 2 -> user
         */
        viewModel.traversAdapterLiveData.observe(this) {
            messageListAdapter?.setData(it)
        }
    }

    private fun traversToAdapterData(conv: List<LCIMConversation>) {
        val list = arrayListOf<String>()
        conv.forEach {
            var uid = ""
            it.members.forEach { id ->
                if (id != IMClientUtils.getCntUser()?.objectId) {
                    uid = id
                }
            }
            list.add(uid)
        }
        // 根据uid查询当前聊天user
        queryContainsUserInfo(list, onSuccess = {
            val packedData = arrayListOf<JSONObject>()
            it.forEachIndexed { index, user ->
                val data = JSONObject()
                data["username"] = user["username"]
                data["avatar"] = user["avatar"]
                data["content"] = conv.getOrNull(index)?.lastMessage?.content
                data["time"] = conv.getOrNull(index)?.lastMessageAt?.time.toString()
                packedData.add(data)
            }
            viewModel.traversAdapterLiveData.postValue(packedData)
        }, onError = {})
    }

    private fun initEventManager() {
        IMEventManager.init(this)
    }

    private fun doNetwork() {
        loginIM()
    }

    private fun initView() {
        userListAdapter = UserListAdapter()
        binding?.rvUserList?.apply {
            adapter = userListAdapter
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        }

        messageListAdapter = MessageListAdapter()
        binding?.rvMessageList?.apply {
            adapter = messageListAdapter
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)

            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    return outRect.set(
                        dp2px(requireContext(), 16),
                        0,
                        dp2px(requireContext(), 16),
                        dp2px(requireContext(), 20)
                    )
                }
            })
        }
    }

    override fun showLoading(msg: String?) {
        if (!msg.isNullOrEmpty()) {
            ToastUtils.show(msg)
        }
    }

    override fun onMessage(
        message: LCIMMessage?,
        conversation: LCIMConversation?,
        client: LCIMClient?
    ) {
        ToastUtils.show("收到消息")
    }

    override fun onInvite(client: LCIMClient?, conversation: LCIMConversation?, operator: String?) {
        ToastUtils.show("收到邀请")
    }

    private fun loginIM() {
        val username = mContext?.getBySp(SP_USER_NAME) ?: ""
        val password = mContext?.getBySp(SP_PASSWORD) ?: ""
        if (username.isNotEmpty() && password.isNotEmpty()) {
            IMClientUtils.loginIM("test123", "123456",
                onSuccess = {
                    viewModel.loginIMLiveData.postValue(it)
                }, onError = {
                    ToastUtils.show(it)
                }
            )
        }
    }

    private fun queryConversation(
        onSuccess: (List<LCIMConversation>) -> Unit,
        onError: (String) -> Unit
    ) {
        IMClientUtils.queryConversation(onSuccess, onError)
    }

    private fun queryContainsUserInfo(
        array: List<String>, onSuccess: (List<LCObject>) -> Unit,
        onError: (String) -> Unit
    ) {
        IMClientUtils.queryContainsUsers(array, onSuccess, onError)
    }
}