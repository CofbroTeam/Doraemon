package com.cofbro.qian.friend.chat.viewholder

import android.view.View
import cn.leancloud.LCObject
import cn.leancloud.im.v2.LCIMMessage
import cn.leancloud.im.v2.messages.LCIMTextMessage
import com.cofbro.qian.R
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ItemChatCookieSignCardMyselfBinding
import com.cofbro.qian.friend.im.IMClientUtils
import com.cofbro.qian.friend.viewholder.DefaultViewHolder
import com.cofbro.qian.utils.CacheUtils
import com.cofbro.qian.utils.MsgFactory
import com.cofbro.qian.view.WaitDialog
import com.hjq.toast.ToastUtils

class CookieSignCardSelfViewHolder(private val binding: ItemChatCookieSignCardMyselfBinding) :
    DefaultViewHolder<LCIMMessage>(binding) {
    override fun bind(position: Int, t: LCIMMessage?) {
        // 文字内容
        val content = (t as? LCIMTextMessage)?.text
        binding.tvCookieCardContent.text = content
        // 时间
        binding.tvTime.text = formatTimestamp(t?.timestamp ?: 0L)
        // 头像
        val avatar = URL.getAvtarImgPath(CacheUtils.cache["uid"] ?: "")
        setImage(binding.avatar, avatar)
        // cookie
        val textMsg = t as? LCIMTextMessage
        val cookie =
            textMsg?.attrs?.getOrDefault(MsgFactory.cookieSign, "").toString()
        itemView.setOnClickListener {
            bindAction(textMsg, cookie)
        }
    }

    private fun bindAction(textMsg: LCIMTextMessage?, cookie: String) {
        if (checkCookieIfValid(textMsg)) {
            findCookieCardInfo(cookie) {
                val agree = it?.get("agree").toString()
                val content =
                    getHolderContext().resources.getString(R.string.cookie_sign_wait_dialog_self_content)
                val title = if (agree.isEmpty()) {
                    getHolderContext().resources.getString(R.string.cookie_sign_wait_dialog_self_title_not_response)
                } else if (agree == "agree") {
                    getHolderContext().resources.getString(R.string.cookie_sign_wait_dialog_self_title_agree)
                } else {
                    getHolderContext().resources.getString(R.string.cookie_sign_wait_dialog_self_title_refuse)
                }
                showWaitDialog(title, content)
            }
        } else {
            ToastUtils.show("卡片已过期~")
        }
    }

    private fun findCookieCardInfo(cookie: String, callback: (LCObject?) -> Unit) {
        val cardId = IMClientUtils.getCntUser()?.objectId + cookie
        IMClientUtils.findCookieCardInfo(cardId, onSuccess = {
            callback(it.getOrNull(0))
        }, onError = {})
    }

    private fun showWaitDialog(title: String, content: String) {
        WaitDialog(getHolderContext()).apply {
            show()
            setTitle(title)
            setContent(content)
            csBottom?.visibility = View.GONE
            tipSureButton?.visibility = View.VISIBLE
            setOnClickTipSureButton {
                dismiss()
            }
        }
    }

    private fun checkCookieIfValid(msg: LCIMTextMessage?): Boolean {
        val cardTime = msg?.timestamp ?: 0L
        val nowTime = System.currentTimeMillis()
        return nowTime - cardTime <= 24 * 60 * 60 * 1000
    }
}