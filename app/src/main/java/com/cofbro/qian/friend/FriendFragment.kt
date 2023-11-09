package com.cofbro.qian.friend

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.qian.databinding.FragmentFriendBinding
import com.cofbro.qian.friend.adapter.MessageListAdapter
import com.cofbro.qian.friend.adapter.UserListAdapter

class FriendFragment : BaseFragment<FriendViewModel, FragmentFriendBinding>() {
    private var userListAdapter: UserListAdapter? = null
    private var messageListAdapter: MessageListAdapter? = null
    override fun onAllViewCreated(savedInstanceState: Bundle?) {
        initView()
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
        }
    }
}