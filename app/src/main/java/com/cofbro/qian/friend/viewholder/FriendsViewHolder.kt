package com.cofbro.qian.friend.viewholder
import android.view.View
import cn.leancloud.LCObject
import com.bumptech.glide.Glide
import com.cofbro.qian.databinding.ItemFriendListBinding
import com.cofbro.qian.databinding.ItemSearchfrendsListBinding
import com.cofbro.qian.friend.search.adapter.Friends
import com.cofbro.qian.friend.search.adapter.FriendsAdapter

class FriendsViewHolder<T : Friends>(private val binding: ItemSearchfrendsListBinding) :
    DefaultViewHolder<T>(binding){
     var itemClick: (() -> Unit?)? = null
    override fun bind(position: Int, t: T?) {
        binding.friendicon.apply {
            Glide.with(this.context)
                .load(t?.src)
                .into(this)
        }
        setImage(binding.friendicon,t?.src)
        binding.friendname.text = t?.name
        binding.isfriends.text = (if (t?.isfriend == true){
            "老朋友~"
        } else {
            "新朋友~"
        }).toString()
        binding.addfriend.apply {
            if (t?.isfriend == true){
                visibility = View.GONE
            }else{
                setItemClickListener {
                    itemClick?.invoke()
                }
            }
        }

    }
      fun setItemClickListener(itemClickListener: () -> Unit){
        itemClick = itemClickListener
    }
}