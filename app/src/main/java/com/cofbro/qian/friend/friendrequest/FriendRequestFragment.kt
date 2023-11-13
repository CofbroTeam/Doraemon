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

class FriendRequestFragment(private val conv: List<LCIMConversation>) : DialogFragment() {
    private val TAG = "FriendRequestFragment"
    private var mAdapter: FriendRequestAdapter? = null
    private var rootView: View? = null
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
                        friendFragment.responseFriendRequest(it, true)
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
                sortConvData(it)
                mAdapter?.setData(data)
            },
            onError = {
                Log.d(TAG, "queryUserInfo: 查询用户信息失败")
            }
        )
    }

    private fun sortConvData(users: List<LCObject>) {
        users.forEachIndexed { index, lcObject ->
            val item = JSONObject()
            val conversation = conv.getOrNull(index)
            item["username"] = lcObject.getString("username")
            item["avatar"] = lcObject.getString("avatar")
            item["uid"] = lcObject.getString("objectId")
            item["isCreator"] = conversation?.creator == IMClientUtils.getCntUser()?.objectId
            item["content"] = "请求添加好友"
            item["status"] = conversation?.get("agree").toString()
            data.add(item)
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