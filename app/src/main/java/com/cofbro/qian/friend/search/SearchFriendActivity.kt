package com.cofbro.qian.friend.search

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.LCObject
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.databinding.ActivitySearchFriendBinding
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.friend.search.adapter.Friends
import com.cofbro.qian.friend.search.adapter.FriendsAdapter
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getStatusBarHeight
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

/**
 * 完成用户搜索功能
 * 延迟1000ms进行用户搜索
 */
class SearchFriendActivity : BaseActivity<SearchFriendViewModel, ActivitySearchFriendBinding>() {
    private var friendsAdapter: FriendsAdapter? = null
    private var friendList: MutableList<Friends> = mutableListOf()
    private var friendsList: ArrayList<String>? = ArrayList()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initArgs()
        initView()
        initEvent()
    }

    private fun initView() {
        initUserSearchBar()
        initRecyclerView()
    }

    private fun initUserSearchBar() {
        val height = getStatusBarHeight(this) + dp2px(
            this,
            6
        )
        val layout = binding?.userSearchBar?.layoutParams as MarginLayoutParams
        layout.topMargin = height
        binding?.userSearchBar?.layoutParams = layout
    }

    private fun initRecyclerView() {
        binding?.rvFriendSearch?.apply {
            friendsAdapter = FriendsAdapter().apply {
                setOnItemClickListener { friend ->
                    sendFriendRequest(friend?.objectId ?: "")
                }
            }
            adapter = friendsAdapter
            layoutManager =
                LinearLayoutManager(this@SearchFriendActivity, RecyclerView.VERTICAL, false)
        }
    }

    private fun initEvent() {
        binding?.tvBack?.setOnClickListener {
            finish()
        }

        binding?.tvDelete?.setOnClickListener {
            clearText()
        }

        binding?.searchFriends?.apply {
            addTextChangedListener(DelayedTextWatcher(this, 1500,
                action = {
                    friendList.clear()
                    searchUser(text.toString())
                },
                onTextCleared = {
                    friendsAdapter?.setData(null)
                }
            ))
        }
    }

    private fun searchUser(username: String) {
        IMClientUtils.querySingleUserByUsernameFuzzy(
            username,
            onSuccess = {
                it.forEach { user ->
                    val contains = friendsList?.contains(user.objectId) ?: false
                    friendList.add(buildUser(user, contains))
                }
                friendsAdapter?.setData(friendList)
            }, onError = {
                ToastUtils.show("好友申请发送失败")
            }
        )
    }

    private fun buildUser(user: LCObject, isFriend: Boolean): Friends {
        return Friends(
            user.objectId ?: "",
            user.getString("username") ?: "",
            user.getString("avatar") ?: "",
            isFriend
        )
    }

    private fun sendFriendRequest(uid: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            IMClientUtils.createNewConversation(
                uid,
                onSuccess = {
                    clearText()
                    ToastUtils.show("好友申请发送成功")
                },
                onError = {
                    ToastUtils.show("好友申请发送失败")
                }
            )
        }
    }

    private fun initArgs() {
        friendsList = intent.getStringArrayListExtra("friends")
    }

    private fun clearText() {
        binding?.searchFriends?.hint = "搜索用户名字"
        binding?.searchFriends?.text?.clear()
    }
}