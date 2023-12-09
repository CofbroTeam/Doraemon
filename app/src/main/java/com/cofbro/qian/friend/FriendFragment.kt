package com.cofbro.qian.friend

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import cn.leancloud.LCObject
import cn.leancloud.LCUser
import cn.leancloud.im.v2.LCIMClient
import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMMessage
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.hymvvmutils.base.BaseFragment
import com.cofbro.hymvvmutils.base.SP_PASSWORD
import com.cofbro.hymvvmutils.base.SP_USER_NAME
import com.cofbro.hymvvmutils.base.getBySp
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.FragmentFriendBinding
import com.cofbro.qian.friend.adapter.MessageListAdapter
import com.cofbro.qian.friend.adapter.UserListAdapter
import com.cofbro.qian.friend.friendrequest.FriendRequestFragment
import com.cofbro.qian.friend.im.IEventCallback
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.friend.im.IMEventManager
import com.cofbro.qian.friend.im.MessageSubscriber
import com.cofbro.qian.friend.login.IMLoginActivity
import com.cofbro.qian.friend.search.SearchFriendActivity
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.MsgFactory
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getStatusBarHeight
import com.hjq.toast.ToastUtils


class FriendFragment : BaseFragment<FriendViewModel, FragmentFriendBinding>(), IEventCallback {
    private val requestCodeLogin = 1001
    private var loginStatus = false
    private var friendList = arrayListOf<LCObject>()
    private var userListAdapter: UserListAdapter? = null
    private var messageListAdapter: MessageListAdapter? = null
    private var friendRequestConv = arrayListOf<LCIMConversation>()
    private var messageConv = arrayListOf<JSONObject>()
    private var distanceForHandleScroll = 0f
    private var toolbarHeight = 0
    private val TAG = "FriendFragment"
    override fun onAllViewCreated(savedInstanceState: Bundle?) {
        checkUserValidAndInit()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkUserValidAndInit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeLogin) {
            val result = data?.getBooleanExtra("login", false) ?: false
            if (result) {
                init(true)
                loginStatus = true
            }
        }
    }

    private fun init(already: Boolean = false) {
        initEventManager()
        initView()
        initObserver()
        doNetwork(already)
        initEvent()
    }

    private fun checkUserValidAndInit() {
        if (loginStatus) return
        val account = requireActivity().getBySp("account")
        val password = requireActivity().getBySp("account_password")
        if (!account.isNullOrEmpty() && !password.isNullOrEmpty()) {
            init()
        } else {
            toIMLoginActivity()
        }
    }

    private fun toIMLoginActivity() {
        val intent = Intent(requireActivity(), IMLoginActivity::class.java)
        startActivityForResult(intent, requestCodeLogin)
    }

    private fun initEvent() {
        binding?.ivMore?.setOnClickListener {
            showFriendRequestFragment(friendRequestConv)
            //responseFriendRequest(friendRequestConv[0], true)
        }

        binding?.tvTitle?.setOnClickListener {

        }

        binding?.editText?.setOnClickListener {
            toUserSearchActivity()
        }
    }

    private fun initView() {
        initToolbar()
        initTopAppbar()
        initHandleScrollView()
        initMessageRecyclerView()
        initUserRecyclerView()
    }

    private fun initMessageRecyclerView() {
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
                    val defaultPadding = dp2px(requireContext(), 16)
                    if (parent.layoutManager?.getPosition(view) == adapter?.itemCount?.minus(
                            1
                        )
                    ) {
                        return outRect.set(
                            defaultPadding,
                            0,
                            defaultPadding,
                            dp2px(requireContext(), 80)
                        )
                    }
                    return outRect.set(
                        defaultPadding,
                        0,
                        defaultPadding,
                        dp2px(requireContext(), 13)
                    )
                }
            })
        }
    }

    private fun initUserRecyclerView() {
        userListAdapter = UserListAdapter()
        binding?.rvUserList?.apply {
            adapter = userListAdapter
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)

            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    if (parent.layoutManager?.getPosition(view) == 0) {
                        return outRect.set(
                            dp2px(requireContext(), 16),
                            0,
                            dp2px(requireContext(), 14),
                            0
                        )
                    }
                    return outRect.set(
                        0,
                        0,
                        dp2px(requireContext(), 14),
                        0
                    )
                }
            })
        }
    }

    private fun updateUsername(username: String) {
        binding?.tvSelfUsername?.text = username
    }

    private fun initTopAppbar() {
        binding?.topAppBar?.apply {
            val layout = layoutParams
            layout?.height = toolbarHeight - 25
            layoutParams = layout
        }
        // 账号
        updateUsername(IMClientUtils.getCntUser()?.username.toString())
        val uid = CacheUtils.cache[Constants.USER.UID] ?: ""
        // 头像
        val options = RequestOptions().transform(
            CenterCrop(),
            RoundedCorners(dp2px(requireContext(), 25))
        )
        Glide.with(this)
            .load(URL.getAvtarImgPath(uid))
            .apply(options)
            .into(binding!!.ivSelfAvatar)
    }

    private fun initHandleScrollView() {
        binding?.handledScrollView?.apply {
            distanceForHandleScroll = (dp2px(requireContext(), 132) + toolbarHeight).toFloat()
            handleScrollDy = distanceForHandleScroll
            setScrollTopListener {
                if (it) {
                    binding?.topAppBar?.visibility = View.GONE
                } else {
                    binding?.topAppBar?.visibility = View.VISIBLE
                    binding?.topAppBar?.setBackgroundColor(Color.parseColor("#03A9F4"))
                }
            }
        }
    }

    private fun initToolbar() {
        // height of toolbar
        binding?.toolBar?.apply {
            toolbarHeight = getStatusBarHeight(requireContext()) + dp2px(
                requireContext(),
                66
            )
            val csLayout = layoutParams
            csLayout.height = toolbarHeight
        }
    }

    private fun initObserver() {
        // 登录即时通讯服务
        viewModel.loginIMLiveData.observe(this) {
            // 更新名字
            updateUsername(IMClientUtils.getCntUser()?.username.toString())
            // 查询好友
            loadUserList()
            // 查询所有会话
            queryConversation(
                onSuccess = {
                    sortConvData(it)
                },
                onError = {
                    ToastUtils.show(it)
                }
            )
        }

        /**
         * data分为两部分获取
         * 1 -> conv
         * 2 -> user
         */
        viewModel.realConversationLiveData.observe(this) {
            messageListAdapter?.setData(it)
        }

        viewModel.friendRequestLiveData.observe(this) {
            if (it.isNotEmpty()) {
                binding?.tvMock?.visibility = View.VISIBLE
            } else {
                binding?.tvMock?.visibility = View.GONE
            }
        }
    }

    /**
     * 将和自己有关的所有会话数据分成两种数据
     * 1 -> 未响应的好友请求
     * 2 -> 真实的会话
     */
    private fun sortConvData(conv: List<LCIMConversation>?) {
        if (conv == null) return
        val convList = ArrayList(conv)
        val queryUserInfoUid = arrayListOf<String>()
        conv.forEach {
            val status = it["agree"] ?: ""
            if (status == IMClientUtils.IMConstants.NOT_RESPONSE) {
                // 移除掉未响应的好友请求
                convList.remove(it)
                friendRequestConv.add(it)
            } else {
                val uid = getTargetUid(it)
                queryUserInfoUid.add(uid)
            }
        }
        // 将渲染好友请求的逻辑分离出去
        viewModel.friendRequestLiveData.postValue(friendRequestConv)
        // 根据uid查询当前聊天user
        queryContainsUserInfo(queryUserInfoUid, onSuccess = {
            it.forEachIndexed { index, user ->
                val username = user["username"].toString()
                val url = user["avatar"].toString()
                val data =
                    MsgFactory.createConversationMsg(convList.getOrNull(index), url, username)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)
                messageConv.add(data)

            }
            viewModel.realConversationLiveData.postValue(messageConv)
        }, onError = {})
    }

    private fun initEventManager() {
        IMClientUtils.init()
        IMEventManager.init(this)
    }

    private fun doNetwork(already: Boolean) {
        loginIM(already)
    }

    private fun loadUserList() {
        IMClientUtils.queryToFindExistFriend(
            onSuccess = {
                formatUsersInfo(it)
                friendList.addAll(it)
                userListAdapter?.setData(it)
            },
            onError = {
                Log.d(TAG, "loadUserList: 加载好友失败")
            }
        )
    }

    private fun formatUsersInfo(users: List<LCObject>) {
        users.forEach {
            var name = ""
            var url = ""
            var uid = ""
            if (it.getString("ownerId") == IMClientUtils.getCntUser()?.objectId.toString()) {
                url = it.getString("targetAvatar") ?: ""
                name = it.getString("targetName") ?: ""
                uid = it.getString("targetId") ?: ""
            } else {
                url = it.getString("ownerAvatar") ?: ""
                name = it.getString("owner") ?: ""
                uid = it.getString("ownerId") ?: ""
            }
            it.put("url", url)
            it.put("name", name)
            it.put("uid", uid)
        }
    }

    private fun clearText() {
        binding?.editText?.hint = "搜索用户名字"
//        binding?.editText?.text?.clear()
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
        Log.d(TAG, "onMessage: 收到消息")
        insertUserListIfNewFriend(message) { username, url ->
            insertMessageAccordingToConv(conversation, username, url)
        }
        MessageSubscriber.dispatch(conversation, message)
    }

    private fun insertUserListIfNewFriend(
        message: LCIMMessage?,
        onSuccess: (String, String) -> Unit
    ) {
        friendList.forEach {
            if (message?.from == (it.getString("uid") ?: "")) {
                // 是好友
                val name = it.getString("name") ?: ""
                val avatar = it.getString("url") ?: ""
                onSuccess(name, avatar)
                return
            }
        }
        IMClientUtils.queryContainUserForConversation(message?.from ?: "",
            onSuccess = {
                it.getOrNull(0)?.apply {
                    val avatar = getString("avatar")
                    val name = getString("username")
                    put("url", avatar)
                    put("name", name)
                    put("uid", objectId)
                    onSuccess(name, avatar)
                    friendList.add(0, this)
                    userListAdapter?.insertData(this)
                }
            }, onError = {}
        )
    }

    private fun insertMessageAccordingToConv(
        conversation: LCIMConversation?,
        username: String = "",
        url: String = ""
    ) {
        notifyConversationMsgChanged(conversation)
        // 如果找不到一样的，说明列表中没有该对话，应该将其插入对话列表中
        val data = MsgFactory.createConversationMsg(conversation, url, username)
        messageConv.add(0, data)
        messageListAdapter?.insertBeforeFirst(data)
    }

    fun notifyConversationMsgChanged(conversation: LCIMConversation?) {
        messageConv.forEachIndexed { index, convItem ->
            val conv = convItem.getObject("conv", LCIMConversation::class.java)
            if (conversation?.conversationId == conv.conversationId) {
                convItem["conv"] = conversation
                convItem["content"] = conversation?.lastMessage?.content.toString()
                convItem["time"] = conversation?.lastMessageAt?.time.toString()
                convItem["unReadCount"] = conversation?.unreadMessagesCount.toString()
                messageListAdapter?.notifyItemChanged(index)
                return
            }
        }
    }

    override fun onInvite(client: LCIMClient?, conversation: LCIMConversation?, operator: String?) {
        val status = conversation?.get("agree")
        if (status == IMClientUtils.IMConstants.NOT_RESPONSE) {
            friendRequestConv.add(conversation)
            // 将渲染好友请求的逻辑分离出去
            viewModel.friendRequestLiveData.postValue(friendRequestConv)
        }
    }

    override fun onInfoChanged(
        client: LCIMClient?,
        conversation: LCIMConversation?,
        attr: cn.leancloud.json.JSONObject?,
        operator: String?
    ) {
        ToastUtils.show("属性改变")
    }

    private fun loginIM(alreadyLogin: Boolean = false) {
        if (alreadyLogin) {
            viewModel.loginIMLiveData.postValue(LCUser())
            return
        }
        val username = mContext?.getBySp("account") ?: ""
        val password = mContext?.getBySp("account_password") ?: ""
        if (username.isNotEmpty() && password.isNotEmpty()) {
            IMClientUtils.loginIM(username, password,
                onSuccess = {
                    viewModel.loginIMLiveData.postValue(it)
                },
                onError = {
                    ToastUtils.show(it)
                }
            )
        }
    }

    private fun queryConversation(
        onSuccess: (List<LCIMConversation>?) -> Unit,
        onError: (String) -> Unit
    ) {
        IMClientUtils.queryConversation(onSuccess, onError)
    }

    private fun queryContainsUserInfo(
        array: List<String>,
        onSuccess: (List<LCObject>) -> Unit,
        onError: (String) -> Unit
    ) {
        IMClientUtils.queryContainsUsersForConversation(array, onSuccess, onError)
    }

    /**
     * 处理好友请求
     * @param conversation 此处好友请求的会话
     * @param accept 是否同意添加好友
     */
    fun responseFriendRequest(conversation: LCIMConversation, accept: Boolean) {
        // 1. 将对话的状态改为已响应
        IMClientUtils.updateConversationInfo(conversation, accept, onSuccess = {}, onError = {})
        if (accept) {
            // 2.1 如果同意好友后，向对方发送一条消息，表示已成为好友
            IMClientUtils.sendMsg(conversation, "我们已经成为好友啦~",
                onSuccess = {
                    Log.d(TAG, "responseFriendRequest: 问候语发送成功")
                    // 2.2 将好友请求会话remove
                    friendRequestConv.remove(conversation)
                    // 2.3 将此次的conversation渲染到列表中
                    insertConv(conversation) {
                        // 2.4 补全Relation表中的关系
                        saveFriendRelation(it)
                    }

                },
                onError = {
                    Log.d(TAG, "responseFriendRequest: 问候语发送失败")
                }
            )
        }
    }

    fun insertDataIntoUserList(data: List<LCObject>) {
        friendList.addAll(0, data)
        userListAdapter?.insertItemRange(data)
    }

    /**
     * 将conversation插入到聊天列表中
     */
    private fun insertConv(conversation: LCIMConversation, onSuccess: (LCObject) -> Unit) {
        val uid = getTargetUid(conversation)
        IMClientUtils.queryContainUserForConversation(
            uid,
            onSuccess = {
                val user = it.getOrNull(0) ?: LCUser()
                val username = user.username
                val url = user["avatar"].toString()
                val data = MsgFactory.createConversationMsg(conversation, url, username)
                // 更新数据源
                messageConv.add(data)
                messageListAdapter?.insertBeforeFirst(data)
                onSuccess(user)
            }, onError = {
                Log.d(TAG, "insertConv: 查询单个用户失败")
            }
        )
    }

    /**
     * 更新云端的Relation表，将好友关系添加进去
     */
    private fun saveFriendRelation(target: LCObject) {
        val uid = CacheUtils.cache["uid"] ?: ""
        val map = hashMapOf<String, String>()
        map["targetAvatar"] = target["avatar"]?.toString() ?: ""
        map["targetId"] = target["objectId"]?.toString() ?: ""
        map["targetName"] = target["username"]?.toString() ?: ""
        map["owner"] = IMClientUtils.getCntUser()?.username ?: ""
        map["ownerId"] = IMClientUtils.getCntUser()?.objectId ?: ""
        map["ownerAvatar"] = URL.getAvtarImgPath(uid)
        viewModel.saveFriendRelation(map) {
            Log.d(TAG, "saveFriendRelation: Relation更新成功！")
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

    private fun showFriendRequestFragment(conv: List<LCIMConversation>) {
        val fragment = FriendRequestFragment(conv)
        fragment.show(requireActivity().supportFragmentManager, "AdviceFragment")
    }

    private fun toUserSearchActivity() {
        val intent = Intent(requireActivity(), SearchFriendActivity::class.java)
        val friendsObjects: ArrayList<String> = arrayListOf()
        friendList.forEach {
            friendsObjects.add(it.getString("uid"))
        }
        intent.putExtra("friends", friendsObjects)
        startActivity(intent)
    }
}