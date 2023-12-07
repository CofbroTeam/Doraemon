package com.cofbro.qian.friend.im

import android.util.Log
import cn.leancloud.LCException
import cn.leancloud.LCObject
import cn.leancloud.LCQuery
import cn.leancloud.LCUser
import cn.leancloud.im.v2.LCIMClient
import cn.leancloud.im.v2.LCIMClientEventHandler
import cn.leancloud.im.v2.LCIMConversation
import cn.leancloud.im.v2.LCIMException
import cn.leancloud.im.v2.LCIMMessage
import cn.leancloud.im.v2.callback.LCIMClientCallback
import cn.leancloud.im.v2.callback.LCIMConversationCallback
import cn.leancloud.im.v2.callback.LCIMConversationCreatedCallback
import cn.leancloud.im.v2.callback.LCIMConversationQueryCallback
import cn.leancloud.im.v2.callback.LCIMMessageUpdatedCallback
import cn.leancloud.im.v2.callback.LCIMMessagesQueryCallback
import cn.leancloud.im.v2.messages.LCIMTextMessage
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


object IMClientUtils {
    private val TAG = "IMClientUtils"

    object IMConstants {
        const val NOT_RESPONSE = "notResponse"
        const val AGREED = "agreed"
        const val REFUSED = "refused"
    }

    private var client: LCIMClient? = null
    private var user: LCUser? = null

    fun init() {
        LCIMClient.setClientEventHandler(object : LCIMClientEventHandler() {
            override fun onConnectionPaused(client: LCIMClient?) {
                Log.d(TAG, "onConnectionPaused: ")
            }

            override fun onConnectionResume(client: LCIMClient?) {
                Log.d(TAG, "onConnectionResume: ")
            }

            override fun onClientOffline(client: LCIMClient?, code: Int) {
                Log.d(TAG, "onClientOffline: ")
            }

        })
    }

    fun getIMClient(): LCIMClient? {
        return client
    }

    fun getCntUser(): LCUser? {
        return user
    }

    /**
     * 登录即时通信
     * @param lUsername lcUsername
     * @param lPassword lcPassword
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun loginIM(
        lUsername: String,
        lPassword: String,
        onSuccess: (LCUser) -> Unit,
        onError: (String) -> Unit
    ) {
        LCUser.logIn(lUsername, lPassword).subscribe(DefaultObserver<LCUser>(onSuccess = {
            val lcIMClient: LCIMClient = LCIMClient.getInstance(it)
            lcIMClient.open(object : LCIMClientCallback() {
                override fun done(avimClient: LCIMClient, e: LCIMException?) {
                    client = avimClient
                    user = it
                    onSuccess.invoke(it)
                }
            })
        }, onError))
    }

    /**
     * 创建对话
     * @param uid objectId of target user
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun findExistConversationOrCreate(
        uid: String,
        onSuccess: (LCIMConversation) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        getIMClient()?.createConversation(
            mutableListOf(uid), "${getCntUser()?.objectId ?: ""} & $uid", null, false, true,
            object : LCIMConversationCreatedCallback() {
                override fun done(conversation: LCIMConversation, e: LCIMException?) {
                    if (e == null) {
                        onSuccess(conversation)
                    } else {
                        onError(e.message.toString())
                    }
                }
            })
    }

    /**
     * 创建新的对话
     * @param uid objectId of target user
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun createNewConversation(
        uid: String,
        onSuccess: (LCIMConversation?) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        getIMClient()?.createConversation(
            mutableListOf(uid), "${getCntUser()?.objectId ?: ""} & $uid", null, false, false,
            object : LCIMConversationCreatedCallback() {
                override fun done(conversation: LCIMConversation?, e: LCIMException?) {
                    if (e == null) {
                        onSuccess(conversation)
                    } else {
                        onError(e.message.toString())
                    }
                }
            })
    }

    /**
     * 发送消息
     * @param conversation the LcConversation after login
     * @param text text string you want to send
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun sendMsg(
        conversation: LCIMConversation,
        text: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val msg = LCIMTextMessage()
        msg.text = text
        conversation.sendMessage(msg, object : LCIMConversationCallback() {
            override fun done(e: LCIMException?) {
                if (e == null) {
                    onSuccess()
                } else {
                    onError(e.message.toString())
                }
            }
        })
    }

    /**
     * 发送消息
     * @param conversation the LcConversation after login
     * @param text text string you want to send
     * @param ext the ext attrs for this message
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun sendMsg(
        conversation: LCIMConversation,
        text: String,
        ext: HashMap<String, Any>,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val msg = LCIMTextMessage()
        msg.text = text
        msg.attrs = ext
        conversation.sendMessage(msg, object : LCIMConversationCallback() {
            override fun done(e: LCIMException?) {
                if (e == null) {
                    onSuccess()
                } else {
                    onError(e.message.toString())
                }
            }
        })
    }

    /**
     * 发送消息
     * @param conversation the LcConversation after login
     * @param oldMsg the oldMessage you want to modify
     * @param newMsg the newMessage
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun updateMsg(
        conversation: LCIMConversation?,
        oldMsg: LCIMMessage,
        newMsg: LCIMMessage,
        onSuccess: (LCIMMessage?) -> Unit,
        onError: (String?) -> Unit
    ) {
        conversation?.updateMessage(oldMsg, newMsg, object : LCIMMessageUpdatedCallback() {
            override fun done(message: LCIMMessage?, e: LCException?) {
                if (e == null) {
                    onSuccess(message)
                } else {
                    onError(e.message)
                }
            }
        })
    }

    /**
     * 查询和自己相关的所有对话
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun queryConversation(onSuccess: (List<LCIMConversation>?) -> Unit, onError: (String) -> Unit) {
        val query = getIMClient()?.conversationsQuery
        query?.setQueryPolicy(LCQuery.CachePolicy.NETWORK_ELSE_CACHE)
        query?.whereContainedIn("m", mutableListOf(getCntUser()?.objectId ?: ""))
        query?.findInBackground(object : LCIMConversationQueryCallback() {
            override fun done(convs: List<LCIMConversation>?, e: LCIMException?) {
                if (e == null) {
                    onSuccess(convs)
                } else {
                    onError(e.message.toString())
                }
            }
        })
    }

    /**
     * 从新到旧，查询历史消息
     * @param conv LCIMConversation
     * @param count the count of messages you want to query
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun queryHistoryMessage(
        conv: LCIMConversation,
        count: Int = 20,
        onSuccess: (List<LCIMMessage>?) -> Unit,
        onError: (String) -> Unit
    ) {
        conv.queryMessages(count, object : LCIMMessagesQueryCallback() {
            override fun done(messages: List<LCIMMessage>?, e: LCIMException?) {
                if (e == null) {
                    onSuccess(messages)
                } else {
                    onError(e.message.toString())
                }
            }
        })
    }

    /**
     * 从新到旧，查询指定消息前的历史消息
     * @param conv LCIMConversation
     * @param count the count of messages you want to query
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun queryHistoryMessage(
        conv: LCIMConversation,
        count: Int = 20,
        oldMsg: LCIMMessage,
        onSuccess: (List<LCIMMessage>?) -> Unit,
        onError: (String) -> Unit
    ) {
        conv.queryMessages(
            oldMsg.messageId,
            oldMsg.timestamp,
            count,
            object : LCIMMessagesQueryCallback() {
                override fun done(messages: List<LCIMMessage>?, e: LCIMException?) {
                    if (e == null) {
                        onSuccess(messages)
                    } else {
                        onError(e.message.toString())
                    }
                }
            })
    }

    /**
     * 查询所有参与过的对话
     * @param array the condition of query array
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun queryContainsUsersForConversation(
        array: List<String>,
        onSuccess: (List<LCObject>) -> Unit,
        onError: (String) -> Unit
    ) {
        if (array.isEmpty()) return
        val queryList = arrayListOf<LCQuery<LCObject>>()
        array.forEach {
            val query = LCQuery<LCObject>("_User")
            query.whereEqualTo("objectId", it)
            queryList.add(query)
        }
        val queryPacket = LCQuery.or(queryList)
        queryPacket.findInBackground()
            .subscribe(DefaultObserver<List<LCObject>>(onSuccess, onError))
    }

    /**
     * 查询所有参与过的对话
     * @param uid the unique identification of a user
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun queryContainUserForConversation(
        uid: String,
        onSuccess: (List<LCUser>) -> Unit,
        onError: (String) -> Unit
    ) {
        val query = LCQuery<LCUser>("_User")
        query.whereEqualTo("objectId", uid)
        query.findInBackground().subscribe(DefaultObserver<List<LCUser>>(onSuccess, onError))
    }

    /**
     * 搜索好友
     * @param username the unique identification of a user
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun querySingleUserByUsernameFuzzy(
        username: String,
        onSuccess: (List<LCObject>) -> Unit,
        onError: (String) -> Unit
    ) {
        val query = LCQuery<LCObject>("_User")
        query.whereContains("username", username)
        query.findInBackground().subscribe(DefaultObserver<List<LCObject>>(onSuccess, onError))
    }

    /**
     * 加载好友
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun queryToFindExistFriend(onSuccess: (List<LCObject>) -> Unit, onError: (String) -> Unit) {
        val query1 = LCQuery<LCObject>("Relation")
        query1.whereEqualTo("ownerId", getCntUser()?.objectId ?: "")

        val query2 = LCQuery<LCObject>("Relation")
        query2.whereEqualTo("targetId", getCntUser()?.objectId ?: "")

        val query = LCQuery.or(listOf(query1, query2))
        query.findInBackground().subscribe(DefaultObserver<List<LCObject>>(onSuccess, onError))
    }

    /**
     * 更新cookie卡片
     * @param conversation conversation
     * @param accept status of agree signing
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun updateConversationInfo(
        conversation: LCIMConversation,
        accept: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val value = if (accept) IMConstants.AGREED else IMConstants.REFUSED
        conversation.set("agree", value)
        conversation.updateInfoInBackground(object : LCIMConversationCallback() {
            override fun done(e: LCIMException?) {
                if (e == null) {
                    onSuccess()
                } else {
                    onError(e.message.toString())
                }
            }
        })
    }

    /**
     * 创建cookie卡片
     * @param cardId userId + cookie
     * @param cookie cookie
     * @param onSuccess success callback
     * @param onError error callback
     */
    fun createCookieCard(
        cardId: String,
        cookie: String,
        onSuccess: (LCObject) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val todo = LCObject("CookieCard")
        todo.put("cardId", cardId)
        todo.put("cookie", cookie)
        todo.put("agree", "")
        todo.saveInBackground().subscribe(DefaultObserver<LCObject>(onSuccess, onError))
    }

    fun findCookieCardInfo(
        cardId: String,
        onSuccess: (List<LCObject>) -> Unit,
        onError: (String) -> Unit
    ) {
        val query = LCQuery<LCObject>("CookieCard")
        query.whereEqualTo("cardId", cardId)
        query.findInBackground().subscribe(DefaultObserver<List<LCObject>>(onSuccess, onError))
    }

    fun updateCookieCardInfo(
        objectId: String,
        ifAgree: Boolean,
        onSuccess: (LCObject) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val agree = if (ifAgree) "agree" else "refuse"
        val todo = LCObject.createWithoutData("CookieCard", objectId)
        todo.put("agree", agree)
        todo.saveInBackground().subscribe(DefaultObserver<LCObject>(onSuccess, onError))
    }

    class DefaultObserver<T : Any>(
        private val onSuccess: ((T) -> Unit)? = null,
        private val onError: ((String) -> Unit)? = null
    ) : Observer<T> {
        override fun onSubscribe(d: Disposable) {
        }

        override fun onError(e: Throwable) {
            onError?.invoke(e.message.toString())
        }

        override fun onComplete() {
        }

        override fun onNext(t: T) {
            onSuccess?.invoke(t)
        }

    }
}