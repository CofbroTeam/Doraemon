package com.cofbro.qian.friend.chat.viewholder

import android.content.Context
import cn.leancloud.LCObject
import cn.leancloud.im.v2.LCIMMessage
import cn.leancloud.im.v2.messages.LCIMTextMessage
import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.databinding.ItemChatCookieSignCardUserSendBinding
import com.cofbro.qian.friend.chat.ChatAdapter
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.friend.viewholder.DefaultViewHolder
import com.cofbro.qian.utils.AccountManager
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.MsgFactory
import com.cofbro.qian.utils.getStringExt
import com.cofbro.qian.view.dialog.WaitDialog
import com.hjq.toast.ToastUtils
import kotlin.concurrent.thread

class CookieSignCardUserViewHolder(
    private val binding: ItemChatCookieSignCardUserSendBinding,
    private val adapter: ChatAdapter,
    private val avatar: String
) :
    DefaultViewHolder<LCIMMessage>(binding) {
    private var pos = 0

    override fun bind(position: Int, t: LCIMMessage?) {
        pos = position
        // 文字内容
        val content = (t as? LCIMTextMessage)?.text
        binding.tvCookieCardContent.text = content
        // 时间
        binding.tvTime.text = formatTimestamp(t?.timestamp ?: 0L)
        // 头像
        setImage(binding.avatar, avatar)
        val textMsg = t as? LCIMTextMessage
        // cookie
        val cookie =
            textMsg?.attrs?.getOrDefault(MsgFactory.cookieSign, "").toString()
        itemView.setOnClickListener {
            showWaitDialog(textMsg, cookie)
        }
    }

    private fun showWaitDialog(msg: LCIMTextMessage?, cookie: String) {
        WaitDialog(getHolderContext()).apply {
            setOnClickPositiveButton {
                agreeCookieSignCard(msg, cookie, true)
                dismiss()
            }
            setOnClickNegativeButton {
                agreeCookieSignCard(msg, cookie, false)
                dismiss()
            }
            show()
        }
    }

    private fun agreeCookieSignCard(msg: LCIMTextMessage?, cookie: String, ifAgree: Boolean) {
        if (msg == null) return
        // 首先判断卡片是否过期
        if (checkCookieIfValid(msg)) {
            // agree，云端记录是否被同意
            findCookieCardInfo(cookie) {
                val agree = it?.get("agree").toString()
                val cookieCardId = it?.objectId ?: ""
                if (agree.isEmpty() || agree == "agree") {
                    // 检测本地是否有cookie记录
                    writeCookieAccountIfNeeded(getHolderContext(), cookie, avatar, msg.timestamp)
                    if (agree == "agree") {
                        ToastUtils.show("你已经同意过了~")
                        return@findCookieCardInfo
                    }
                    updateCookieCard(cookieCardId, ifAgree)
                } else if (agree == "refuse") {
                    ToastUtils.show("你已经拒绝过了~")
                }
            }
        } else {
            ToastUtils.show("代签卡片已过期~")
        }

    }

    private fun updateMsg(msg: LCIMTextMessage, ifAgree: Boolean) {
        val newMsg = MsgFactory.mockCookieSignLCMessage(msg, ifAgree)
        val conversation = adapter.conv
        IMClientUtils.updateMsg(conversation, msg, newMsg,
            onSuccess = {
                adapter.replaceMsg(it, pos)
                ToastUtils.show("已同意为好友代签，有效期1天")
            },
            onError = {}
        )
    }

    private fun writeCookieAccountIfNeeded(
        context: Context,
        cookie: String,
        avatar: String,
        time: Long
    ) {
        thread {
            val cookieSignData =
                AccountManager.loadAllAccountData(context, Constants.RecycleJson.COOKIE_JSON_DATA)
            // 检测本地是否有cookie记录
            if (checkCookieIfValidInLocal(cookieSignData, cookie)) {
                val account = AccountManager.buildCookieSignAccount(cookie, avatar, time)
                val data = AccountManager.bindAccounts(
                    context,
                    cookieSignData,
                    account,
                    Constants.RecycleJson.COOKIE_JSON_DATA
                )?.toJSONString() ?: ""
                AccountManager.updateAccountData(
                    context,
                    data,
                    Constants.RecycleJson.COOKIE_JSON_DATA
                )
            }
        }
    }

    private fun checkCookieIfValidInLocal(accounts: JSONObject, cookie: String): Boolean {
        val a = accounts.getJSONArray(Constants.Account.USERS)
        a?.forEach {
            val jsonObject = it as? JSONObject
            if (jsonObject?.getStringExt(Constants.Account.COOKIE) == cookie) {
                return false
            }
        }
        return true
    }

    private fun checkCookieIfValid(msg: LCIMTextMessage?): Boolean {
        val cardTime = msg?.timestamp ?: 0L
        val nowTime = System.currentTimeMillis()
        return nowTime - cardTime <= 24 * 60 * 60 * 1000
    }

    private fun targetUserId(): String {
        var targetId = ""
        val curUserId = IMClientUtils.getCntUser()?.objectId
        adapter.conv?.members?.forEach {
            if (it != curUserId) {
                targetId = it
            }
        }
        return targetId
    }

    private fun findCookieCardInfo(cookie: String, callback: (LCObject?) -> Unit) {
        val cardId = targetUserId() + cookie
        IMClientUtils.findCookieCardInfo(cardId, onSuccess = {
            callback(it.getOrNull(0))
        }, onError = {})
    }

    private fun updateCookieCard(objectId: String, ifAgree: Boolean) {
        IMClientUtils.updateCookieCardInfo(objectId, ifAgree, onSuccess = {
            ToastUtils.show("同意成功~")
        }, onError = {
            ToastUtils.show("操作失败")
        })
    }
}