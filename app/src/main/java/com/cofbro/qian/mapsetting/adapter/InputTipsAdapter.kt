package com.cofbro.qian.mapsetting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.help.Tip

import com.cofbro.qian.R
import com.cofbro.qian.databinding.ItemInputtipsListBinding
import com.cofbro.qian.databinding.ItemTaskListBinding
import kotlinx.coroutines.flow.combine

/**
 * 输入提示adapter，展示item名称和地址
 */
class InputTipsAdapter(val context: Context,val currentTip:MutableList<Tip>): RecyclerView.Adapter<InputTipsAdapter.InputViewHolder>()  {
    private var itemClick: ((itemTip:Tip) -> Unit?)? = null
    class InputViewHolder(private val binding: ItemInputtipsListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int, currentTip:MutableList<Tip>,itemClick: ((itemTip:Tip) -> Unit?)? ){
            binding.name.text = currentTip[position].name
            binding.adress.text = currentTip[position].address
            binding.adapterS.setOnClickListener {
                itemClick?.invoke(currentTip[position])
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InputViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemInputtipsListBinding.inflate(inflater, parent, false)
        return InputViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InputViewHolder, position: Int) {
       holder.bind(position,currentTip,itemClick)
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getItemCount(): Int {
        return currentTip.size
    }
    fun setItemClickListener(itemClickListener: (itemTip:Tip) -> Unit){
        itemClick = itemClickListener
    }


}