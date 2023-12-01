package com.cofbro.qian.mapsetting

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.cofbro.qian.mapsetting.util.Constants
import com.cofbro.qian.mapsetting.util.ToastUtil
import com.cofbro.qian.mapsetting.viewmodel.InputTipViewModel
import com.cofbro.qian.utils.TipUtils


class InputTipsActivity : BaseActivity<InputTipViewModel,ActivityInputTipsBinding>(), SearchView.OnQueryTextListener,
    InputtipsListener, View.OnClickListener {
    var aid:String? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {


        initArgs()
        initSearchView()
        initViewClick()

    }
    private fun initArgs(){
        val intent = intent
        aid = intent.getStringExtra("aid")
        /**
         * 传递数据
         */
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
            viewModel.mCurrentTipList = tipList
            val listString: MutableList<String> = ArrayList()
            for (i in tipList.indices) {
                listString.add(tipList[i].name)
            }
            viewModel.mIntipAdapter = InputTipsAdapter(
                this, currentTip = viewModel.mCurrentTipList!!
            )
            binding?.inputtipList?.apply {
                adapter = viewModel.mIntipAdapter
                layoutManager = LinearLayoutManager(this@InputTipsActivity, RecyclerView.VERTICAL,false)
            }
            viewModel.mIntipAdapter?.setItemClickListener {
                if (viewModel.mCurrentTipList != null) {
                    /**
                     *  实现跳转
                     */

                    val intent = Intent(this, MapActivity::class.java)

                    intent.putExtra(Constants.EXTRA_TIP, TipUtils.TipParseToArray(it))
                    intent.putExtra("aid",aid)
                    /**
                     * 保存并传递数据
                     */
                    setResult(100,intent)
                    finish()
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
            if (viewModel.mIntipAdapter != null && viewModel.mCurrentTipList != null) {
                viewModel.mCurrentTipList!!.clear()
                viewModel.mIntipAdapter!!.notifyDataSetChanged()
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