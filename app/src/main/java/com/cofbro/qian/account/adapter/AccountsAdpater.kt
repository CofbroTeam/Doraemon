package com.cofbro.qian.account.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.help.Tip
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cofbro.qian.account.manager.User
import com.cofbro.qian.data.URL
import com.cofbro.qian.databinding.ItemAccountsListBinding
import com.cofbro.qian.utils.dp2px


class AccountsAdpater(val context: Context, var accounts: MutableList<User>):RecyclerView.Adapter<AccountsAdpater.AccountsHolder>() {
    private var itemClick: ((user:User) -> Unit)? = null
    private var delete = false
    private var deleteLister: ((position:Int) -> Unit)? = null
    inner class AccountsHolder(private val binding: ItemAccountsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /*
        提取数据进行
         */
        fun bind(position: Int){
            val item = accounts [position]
            val options = RequestOptions().transform(
                CenterCrop(),
                RoundedCorners(dp2px(binding.root.context, 5))
            )
            Glide.with(binding.root)
                .load(URL.getAvtarImgPath(item.uid))
                .apply(options)
                .into(binding.ivTaskImage)
            binding.tvTitle.text = item.user
            binding.delete.visibility = if (delete){View.GONE}else{View.VISIBLE}
            binding.delete.setOnClickListener {
                deleteLister?.invoke(position)
            }
            itemView.setOnClickListener {
                itemClick?.invoke(item)
            }

        }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsHolder {
          val  inflater = LayoutInflater.from(parent.context)
          val binding = ItemAccountsListBinding.inflate(inflater,parent,false)
          return AccountsHolder(binding)
    }

    override fun getItemCount(): Int {
          return accounts.size
    }

    override fun onBindViewHolder(holder: AccountsHolder, position: Int) {
       holder.bind(position)
    }
    fun setItemClickListener(itemClickListener: (user:User) -> Unit) {
        itemClick = itemClickListener
    }
    fun setDeletDisable(ClickListener: ((position:Int) -> Unit)? ){
       deleteLister = ClickListener
    }
    fun showDeletButton(){
        delete = true
    }
}