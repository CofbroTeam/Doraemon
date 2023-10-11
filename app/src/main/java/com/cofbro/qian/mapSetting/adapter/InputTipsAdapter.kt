package com.cofbro.qian.mapSetting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.help.Tip

import com.cofbro.qian.R

/**
 * 输入提示adapter，展示item名称和地址
 */
class InputTipsAdapter(val context: Context,val currentTip:MutableList<Tip>): RecyclerView.Adapter<InputTipsAdapter.MyViewHolder>()  {
   private var itemClick: ((itemTip:Tip) -> Unit?)? = null


    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        var content = itemView.findViewById<LinearLayout>(R.id.adapterS)
        var name = itemView.findViewById<TextView>(R.id.name)
        var adress = itemView.findViewById<TextView>(R.id.adress)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.adapter_inputtips,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = currentTip[position].name
        holder.adress.text = currentTip[position].address
        holder.content.setOnClickListener {

            itemClick?.invoke(currentTip[position])

        }
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