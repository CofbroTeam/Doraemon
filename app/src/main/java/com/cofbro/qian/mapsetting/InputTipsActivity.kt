package com.cofbro.qian.mapsetting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.InputBinding
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.Inputtips.InputtipsListener
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.cofbro.hymvvmutils.base.BaseActivity
import com.cofbro.qian.R
import com.cofbro.qian.databinding.ActivityInputTipsBinding
import com.cofbro.qian.mapsetting.adapter.InputTipsAdapter
import com.cofbro.qian.mapsetting.repository.InputTipRepository
import com.cofbro.qian.mapsetting.util.Constants
import com.cofbro.qian.mapsetting.util.ToastUtil
import com.cofbro.qian.mapsetting.viewmodel.InputTipViewModel
import com.cofbro.qian.utils.TipUtils


class InputTipsActivity : BaseActivity<InputTipViewModel,ActivityInputTipsBinding>(), SearchView.OnQueryTextListener,
    InputtipsListener, View.OnClickListener {
    private var mCurrentTipList: MutableList<Tip>? = null
    private var mIntipAdapter: InputTipsAdapter? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initSearchView()
        initViewClick()
    }
    private fun  initViewClick(){
        binding?.back?.setOnClickListener(this)
    }

    private fun initSearchView() {
        binding?.keyWord?.setOnQueryTextListener(this)
        //设置SearchView默认为展开显示
        binding?.keyWord?.isIconified = false
        binding?.keyWord?.onActionViewExpanded()
        binding?.keyWord?.setIconifiedByDefault(true)
        binding?.keyWord?.isSubmitButtonEnabled = false
    }

    /**
     * 输入提示回调
     *
     * @param tipList
     * @param rCode
     */
    override fun onGetInputtips(tipList: MutableList<Tip>, rCode: Int) {

        if (rCode == 1000) { // 正确返回
            mCurrentTipList = tipList
            val listString: MutableList<String> = ArrayList()
            for (i in tipList.indices) {
                listString.add(tipList[i].name)
            }
            mIntipAdapter = InputTipsAdapter(
                this, currentTip = mCurrentTipList!!
            )
            binding?.inputtipList?.apply {
                adapter = mIntipAdapter
                layoutManager = LinearLayoutManager(this@InputTipsActivity, RecyclerView.VERTICAL,false)
            }
            mIntipAdapter?.setItemClickListener {
                Log.v("ssx","ssx")
                if (mCurrentTipList != null) {
                    /**
                     *  实现跳转
                     */
                    val intent = Intent(this, MapActivity::class.java)
                    intent.putExtra(Constants.EXTRA_TIP, TipUtils().TipParseToArray(it))
                    startActivity(intent)
                }
            }
        } else {
            ToastUtil.showerror(this, rCode)
        }
    }
    /**
     * 按下确认键触发，本例为键盘回车或搜索键
     *
     * @param query
     * @return
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        val intent = Intent()
        intent.putExtra(Constants.KEY_WORDS_NAME, query)
        setResult(MapActivity.RESULT_CODE_KEYWORDS, intent)
        finish()
        return false
    }

    /**
     * 输入字符变化时触发
     *
     * @param newText
     * @return
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        if (!IsEmptyOrNullString(newText)) {
            val inputquery = InputtipsQuery(newText, Constants.DEFAULT_CITY)
            val inputTips = Inputtips(this@InputTipsActivity.applicationContext, inputquery)
            inputTips.setInputtipsListener(this)
            inputTips.requestInputtipsAsyn()
        } else {
            if (mIntipAdapter != null && mCurrentTipList != null) {
                mCurrentTipList!!.clear()
                mIntipAdapter!!.notifyDataSetChanged()
            }
        }
        return false
    }

    override fun onClick(view: View) {
        if (view.id == R.id.back) {
            finish()
        }
    }

    companion object {
        fun IsEmptyOrNullString(s: String?): Boolean {
            return s == null || s.trim { it <= ' ' }.length == 0
        }
    }
}