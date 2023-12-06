package com.cofbro.qian.friend.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.lifecycleScope
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.databinding.ActivitySearchFriendBinding
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.friend.search.adapter.Friends
import com.cofbro.qian.friend.search.adapter.FriendsAdapter
import com.cofbro.qian.mapsetting.adapter.InputTipsAdapter
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 完成用户搜索功能
 * 延迟1000ms进行用户搜索
 */
class SearchFriendActivity : BaseActivity<SearchFriendViewModel,ActivitySearchFriendBinding>() {
    private var FriendsAdapter: FriendsAdapter? = null
    private var FriendList:MutableList<Friends> = mutableListOf()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
          initView()
    }

    private fun initView() {
       binding?.searchFriends?.apply {
           addTextChangedListener(DelayedTextWatcher(this,3000,
               action = {
                   FriendList.clear()
                   searchUser(this.text.toString())
                   FriendsAdapter = FriendsAdapter(this@SearchFriendActivity, FriendList){
                       sendFriendRequest(it.objectId)
                   }
               },
               preAction = {

               }
           ))
       }
       binding?.friendsrecyclerview?.apply {
           adapter = FriendsAdapter
       }
    }
    private fun searchUser(username:String) {
        IMClientUtils.querySingleUserByUsernameFuzzy(
            username,
            onSuccess = {
                it.forEach {Object->
                    FriendList.add(Friends(Object.objectId,Object.getString("username"),Object.getString("avatar")))
                }
            }, onError = {
                ToastUtils.show("好友申请发送失败")
            }
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
    private fun clearText() {
        binding?.searchFriends?.hint = "搜索用户名字"
        binding?.searchFriends?.text?.clear()
    }
}