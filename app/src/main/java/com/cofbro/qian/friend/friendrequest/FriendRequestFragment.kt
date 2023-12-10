package com.cofbro.qian.friend.friendrequest

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.LCObject
import cn.leancloud.im.v2.LCIMConversation
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.R
import com.cofbro.qian.friend.FriendFragment
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.utils.MsgFactory

class FriendRequestFragment(private val conv: List<LCIMConversation>) : DialogFragment() {
    private val TAG = "FriendRequestFragment"
    private var mAdapter: FriendRequestAdapter? = null
    private var rootView: View? = null
    private var usersTemp = ArrayList<LCObject>()
    private val data = arrayListOf<JSONObject>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val height = context?.resources?.displayMetrics?.heightPixels?.minus(100)
        val window = this.dialog?.window
        window?.decorView?.setPadding(0, 0, 0, 0)
        val lp = window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        lp?.height = height ?: WindowManager.LayoutParams.MATCH_PARENT
        lp?.gravity = Gravity.BOTTOM
        lp?.windowAnimations = R.style.BottomDialogAnimation
        window?.attributes = lp
        window?.setBackgroundDrawable(ColorDrawable())
        rootView = layoutInflater.inflate(R.layout.fragment_friend_request, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        doNetwork()
        initView()
    }

    private fun doNetwork() {
        queryUserInfo(conv)
    }

    private fun initView() {
        val rvContent = rootView?.findViewById<RecyclerView>(R.id.rv_content)
        mAdapter = FriendRequestAdapter().apply {
            setOnItemClickListener { pos ->
                findFriendFragment()?.let { friendFragment ->
                    conv.getOrNull(pos)?.let {
                        // 更新聊天列表
                        friendFragment.responseFriendRequest(it, true)
                        // 更新用户列表
                        friendFragment.insertDataIntoUserList(usersTemp)
                    }
                }
            }
        }
        rvContent?.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun queryUserInfo(conv: List<LCIMConversation>) {
        val uidList = arrayListOf<String>()
        conv.forEach {
            uidList.add(getTargetUid(it))
        }
        IMClientUtils.queryContainsUsersForConversation(uidList,
            onSuccess = {
                // 好友申请列表
                sortConvData(it)
                mAdapter?.setData(data)
            },
            onError = {
                Log.d(TAG, "queryUserInfo: 查询用户信息失败")
            }
        )
    }

    private fun sortConvData(users: List<LCObject>) {
        users.forEachIndexed { index, user ->
            // 好友申请列表
            val friendRequestItem = MsgFactory.createFriendRequestMsg(conv.getOrNull(index), user)
            data.add(friendRequestItem)

            // 好友列表
            val userItem = MsgFactory.createUserInfoMsg(user)
            usersTemp.add(userItem)
        }
    }

    private fun getTargetUid(conv: LCIMConversation): String {
        var uid = ""
        conv.members.forEach { id ->
            if (id != IMClientUtils.getCntUser()?.objectId) {
                uid = id
            }
        }
        return uid
    }

    private fun findFriendFragment(): FriendFragment? {
        return requireActivity().supportFragmentManager.findFragmentByTag("FriendFragment") as? FriendFragment
    }

}