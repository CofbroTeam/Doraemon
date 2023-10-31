package com.cofbro.qian.account.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.qian.databinding.ItemAccountsListBinding
import com.cofbro.qian.utils.Constants
import com.cofbro.qian.utils.dp2px
import com.cofbro.qian.utils.getIntExt
import com.cofbro.qian.utils.getJSONArrayExt
import com.cofbro.qian.utils.getStringExt

/**
 * {
 *     "history": "true",
 *     "size": "2"
 *     "users": [
 *         {
 *             "username": "cofbro",
 *             "password": "123456",
 *             "uid": "000",
 *             "fid": "000",
 *             "picUrl": "xxxxxxxxx"
 *         },
 *         {
 *             "username": "cofbro",
 *             "password": "123456",
 *             "uid": "000",
 *             "fid": "000",
 *             "picUrl": "xxxxxxxxx"
 *         }
 *       ]
 * }
 */
class AccountsAdapter : RecyclerView.Adapter<AccountsAdapter.AccountsHolder>() {
    private var accountData: JSONObject? = null
    private var itemClick: ((JSONObject?) -> Unit)? = null
    private var itemLongClick: ((JSONObject?, Int) -> Unit)? = null
    private var onDataChanged: ((JSONObject?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAccountsListBinding.inflate(inflater, parent, false)
        return AccountsHolder(binding)
    }

    override fun getItemCount(): Int {
        return accountData?.getStringExt("size", "0")?.toInt() ?: 0
    }

    override fun onBindViewHolder(holder: AccountsHolder, position: Int) {
        holder.bind(position)
    }

    inner class AccountsHolder(private val binding: ItemAccountsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val itemData =
                accountData?.getJSONArrayExt(Constants.Account.USERS)?.getOrNull(position)
                    ?: JSONObject()
            (itemData as? JSONObject).let { itemValue ->
                val picUrl = itemValue?.getStringExt(Constants.Account.PIC_URL) ?: ""
                val username = itemValue?.getStringExt(Constants.Account.USERNAME) ?: ""
                val uid = itemValue?.getStringExt(Constants.Account.UID) ?: ""

                // 绑定数据
                val options = RequestOptions().transform(
                    CenterCrop(),
                    RoundedCorners(dp2px(binding.root.context, 25))
                )
                Glide.with(binding.root)
                    .load(picUrl)
                    .apply(options)
                    .into(binding.ivTaskImage)
                binding.tvTitle.text = "账号：$username"
                binding.tvTimeLine.text = "uid：$uid"

                itemView.setOnClickListener {
                    itemClick?.invoke(itemValue)
                }

                itemView.setOnLongClickListener {
                    itemLongClick?.invoke(itemValue, position)
                    false
                }
            }
        }
    }

    fun setItemOnLongClickListener(listener: (JSONObject?, Int) -> Unit) {
        itemLongClick = listener
    }

    fun setDataChangedListener(listener: (JSONObject?) -> Unit) {
        onDataChanged = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAccounts(t: JSONObject?) {
        accountData = t
        notifyDataSetChanged()
        onDataChanged?.invoke(accountData)
    }

    fun getData(): JSONObject? {
        return accountData
    }

    fun removeAccount(uid: String) {
        var index = 0
        val array = accountData?.getJSONArrayExt(Constants.Account.USERS)
        array?.let {
            it.forEachIndexed { i, any ->
                val data = any as? JSONObject
                if (data?.getStringExt(Constants.Account.UID) == uid) {
                    index = i
                }
            }
            if (index < it.size) {
                array.removeAt(index)
                accountData?.set(Constants.Account.SIZE, array.size)
                accountData?.set(Constants.Account.USERS, array)
                notifyItemRemoved(index)
                onDataChanged?.invoke(accountData)
            }
        }
    }

    fun addAccount() {
//        if (accountData == null) accountData = JSONObject()
//        val array = accountData?.getJSONArrayExt(Constants.Account.USERS) ?: JSONArray()
//        array.add(position, data)
//        accountData?.set(Constants.Account.SIZE, array.size)
//        accountData?.set(Constants.Account.USERS, array)
        val size = accountData?.getJSONArrayExt(Constants.Account.USERS)?.size ?: 0
        if (size == 0) return
        notifyItemInserted(size - 1)
    }

    fun notifyItemInserted() {
        val size = accountData?.getIntExt(Constants.Account.SIZE).takeIf {
            it != -1
        } ?: 0
        if (size > 0) {
            notifyItemInserted(size - 1)
            onDataChanged?.invoke(accountData)
        }
    }

}