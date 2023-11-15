package com.wbb.xiaodongjia.ui.activity.login

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.wbb.base.observer.ObserverKey
import com.wbb.base.viewbase.BaseDataActivityKt
import com.wbb.base.mvvm.base.BaseViewModel
import com.wbb.base.bean.CountrySortBean
import com.wbb.base.observer.ObserverManger
import com.wbb.base.sidebar.CountryComparator
import com.wbb.base.sidebar.GetCountryNameSort
import com.wbb.base.util.CharacterParserUtil
import com.wbb.base.util.LanguageUtils
import com.wbb.base.wiget.SearchForLayout
import com.wbb.xiaodongjia.R
import com.wbb.xiaodongjia.adapter.CountryCodeAdapter
import kotlinx.android.synthetic.main.activity_choose_layout.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zj on 2021/1/28.
 * 选择国家地区
 */
class ChooseAcountryActivity : BaseDataActivityKt<BaseViewModel>(),
    SearchForLayout.OnEditTextChangedLinsener, View.OnClickListener {
    var allCountryList = ArrayList<CountrySortBean>()

    var characterParserUtil: CharacterParserUtil? = null
    var countryChangeUtil: GetCountryNameSort? = null
    var pinyinComparator: CountryComparator? = null
    private var mCountryCodeAdapter: CountryCodeAdapter? = null

    companion object {
        fun startIntent(context: Context?) {
            val intent = Intent(context, ChooseAcountryActivity::class.java)
            context?.startActivity(intent)
        }
    }

    override fun onReloadData() {
    }

    override val isLoadNotData: Boolean
        get() = false
    override val isShowTitle: Boolean
        get() = true

    override fun getLayoutResId(): Int {
        return R.layout.activity_choose_layout
    }

    override fun initView() {
        setTitleName(LanguageUtils.getString(R.string.hoose_country))
        sfl_search.setOnEditTextChangedLinsener(this)
    }

    override fun initData() {
        pinyinComparator = CountryComparator()
        countryChangeUtil = GetCountryNameSort()
        characterParserUtil = CharacterParserUtil()
        val codeList = resources.getStringArray(R.array.country_code_list_ch)
        //获取普通国家和地区
        val list = IntegrationList(codeList)
        Collections.sort(list, pinyinComparator)
        allCountryList.addAll(list)

        val hotList = resources.getStringArray(R.array.country_code_list_ch_hot)
        val hotEmpList = IntegrationList(hotList, true)
        allCountryList.addAll(0, hotEmpList)
        mCountryCodeAdapter = CountryCodeAdapter(allCountryList, this)
        rl_list.layoutManager = LinearLayoutManager(this)
        rl_list.adapter = mCountryCodeAdapter
    }

    override fun onGetClassTypeNam(): Any {
        return "国家和地区"
    }

    fun IntegrationList(list: Array<String>, isHot: Boolean = false): List<CountrySortBean> {
        val empList = ArrayList<CountrySortBean>()
        for (index in 0..list.size - 1) {
            val item = list.get(index).trim().split("*")
            val countryName = item.get(0)
            val countryNumber = item.get(1)
            val countrySortKey = characterParserUtil?.getSelling(countryName)!!
            val mCountrySortBean = CountrySortBean(countryName, countryNumber, countrySortKey)
            if (isHot) {
                mCountrySortBean.sortLetters = LanguageUtils.getString(R.string.hot)
            } else {
                var sortLetter = countryChangeUtil?.getSortLetterBySortKey(countrySortKey)
                if (sortLetter == null) {
                    sortLetter = countryChangeUtil?.getSortLetterBySortKey(countryName)
                }
                mCountrySortBean.sortLetters = sortLetter
            }

            empList.add(mCountrySortBean)
        }
        return empList
    }

    override fun onInpuTextChangedLinsener(str: String) {
        if (str.isNotEmpty()) {
            // 按照输入内容进行匹配
            val fileterList = countryChangeUtil?.search(
                str,
                allCountryList
            ) as ArrayList<CountrySortBean>
            mCountryCodeAdapter?.setNewData(fileterList)
        } else {
            mCountryCodeAdapter?.setNewData(allCountryList)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_country -> {
                val country = v.tag as CountrySortBean
                ObserverManger.getInstance(ObserverKey.COUNTRY_CODE).notifyObserver(country)
                finish()
            }
        }
    }
}