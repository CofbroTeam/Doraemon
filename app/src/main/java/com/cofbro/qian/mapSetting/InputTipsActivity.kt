package com.cofbro.qian.mapSetting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
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
import com.cofbro.qian.R
import com.cofbro.qian.mapSetting.adapter.InputTipsAdapter
import com.cofbro.qian.mapSetting.util.Constants
import com.cofbro.qian.mapSetting.util.ToastUtil


class InputTipsActivity : Activity(), SearchView.OnQueryTextListener,
    InputtipsListener, OnItemClickListener, View.OnClickListener {
    private var mSearchView: SearchView? = null // 输入搜索关键字
    private var mBack: ImageView? = null
    private var mInputListView: RecyclerView? = null
    private var mCurrentTipList: MutableList<Tip>? = null
    private var mIntipAdapter: InputTipsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_tips)
        initSearchView()
        mInputListView = findViewById(R.id.inputtip_list)
//        mInputListView!!.addOnItemTouchListener(this)
        mBack = findViewById<View>(R.id.back) as ImageView
        mBack!!.setOnClickListener(this)

    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {

        return super.onCreateView(name, context, attrs)
    }
    private fun initSearchView() {
        mSearchView = findViewById<View>(R.id.keyWord) as SearchView
        mSearchView!!.setOnQueryTextListener(this)
        //设置SearchView默认为展开显示
        mSearchView!!.isIconified = false
        mSearchView!!.onActionViewExpanded()
        mSearchView!!.setIconifiedByDefault(true)
        mSearchView!!.isSubmitButtonEnabled = false
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
            mInputListView!!.apply {
                adapter = mIntipAdapter
                layoutManager = LinearLayoutManager(this@InputTipsActivity, RecyclerView.VERTICAL,false)
            }
            mIntipAdapter!!.setItemClickListener {
                Log.v("ssx","ssx")
                if (mCurrentTipList != null) {
                    /**
                     *  实现跳转
                     */
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(Constants.EXTRA_TIP, it)
                    startActivity(intent)
                }
            }



        } else {
            ToastUtil.showerror(this, rCode)
        }
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

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
        setResult(MainActivity.RESULT_CODE_KEYWORDS, intent)
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